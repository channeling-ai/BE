package channeling.be.domain.idea.application;

import channeling.be.domain.auth.domain.CustomUserDetails;
import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.channel.domain.repository.ChannelRepository;
import channeling.be.domain.idea.domain.Idea;
import channeling.be.domain.idea.domain.event.IdeaDeletedEvent;
import channeling.be.domain.idea.domain.repository.IdeaRepository;
import channeling.be.domain.idea.presentation.IdeaConverter;
import channeling.be.domain.idea.presentation.IdeaReqDto;
import channeling.be.domain.idea.presentation.IdeaResDto;
import channeling.be.domain.log.IdeaLogRepository;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.member.domain.SubscriptionPlan;
import channeling.be.global.infrastructure.llm.LlmResDto;
import channeling.be.global.infrastructure.llm.LlmServerUtil;
import channeling.be.response.exception.handler.IdeaHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static channeling.be.response.code.status.ErrorStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class IdeaServiceImpl implements IdeaService {

    private final IdeaRepository ideaRepository;
    private final ChannelRepository channelRepository;
    private final LlmServerUtil llmServerUtil;
    private final IdeaLogRepository ideaLogRepository;
    private final ApplicationEventPublisher eventPublisher;

    private final int IDEA_CURSOR_SIZE = 12;
    private final ZoneId timeZone = ZoneId.of("Asia/Seoul"); // DB 시간 기준

    @Override
    @Transactional
    public IdeaResDto.ChangeIdeaBookmarkRes changeIdeaBookmark(Long ideaId, Member loginMember) {
        //아이디어 조회
        Idea idea = ideaRepository.findWithVideoChannelMemberById(ideaId).orElseThrow(() -> new IdeaHandler(_IDEA_NOT_FOUND));
        //해당 아이디어가 로그인한 멤버의 아이디어인지 확인

        if (!idea.getChannel().getMember().getId().equals(loginMember.getId())) {
            throw new IdeaHandler(_IDEA_NOT_MEMBER);
        }
        //아이디어 북마크 수정 -> 더티체크
        return IdeaConverter.toChangeIdeaBookmarkres(ideaId, idea.switchBookMarked());
    }

    @Override
    public IdeaResDto.GetBookmarkedIdeaListRes getBookmarkedIdeaList(Member loginMember, int page, int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Idea> ideaPage = ideaRepository.findIdeasByMemberId(loginMember.getId(), pageable);
        return IdeaConverter.toBookmarkedIdeaListRes(ideaPage, page, size);

    }

    @Override
    @Transactional
    public void deleteNotBookMarkedIdeas(Member loginMember) {
        List<Idea> ideas = ideaRepository.findByMemberWithoutBookmarked(loginMember.getId());
        log.info("아이디어 {}", ideas);

        ideas.stream().forEach(idea -> {
            eventPublisher.publishEvent(new IdeaDeletedEvent(idea));
        });

        ideaRepository.deleteAll(ideas);
        int deletedCount = ideas.size();

        log.info("Deleted {} unbookmarked ideas for memberId={}", deletedCount, loginMember.getId());
    }

    @Async
    @Override
    @Transactional
    public void deleteNotBookMarkedIdeasAsync(Member loginMember) {
        try {
            int deletedCount = ideaRepository.deleteAllByMemberWithoutBookmarked(loginMember.getId());
            log.info("비동기 삭제 완료 - {} unbookmarked ideas for memberId={}", deletedCount, loginMember.getId());
        } catch (Exception e) {
            log.error("비동기 아이디어 삭제 실패 - memberId: {}, error: {}", loginMember.getId(), e.getMessage(), e);
        }
    }

    @Override
    public List<LlmResDto.CreateIdeasResDto> createIdeas(IdeaReqDto.CreateIdeaReqDto dto, Member member) {

        if (!member.getPlan().equals(SubscriptionPlan.ADMIN)) {
            long currentCount = this.countMonthlyIdeas(member);
            member.checkIdeaCredit(currentCount);
        }

        Channel channel = channelRepository.findByMember(member)
                .orElseThrow(() -> new IdeaHandler(_CHANNEL_NOT_FOUND));

        log.info("아이디어 생성 요청 내용: {}", dto);

        return llmServerUtil.createIdeas(dto, channel);
    }

    // 아이디어 페이지 - 채널 기반 아이디어 목록 조회
    // 로그인 시점 이후 생성된 아이디어 목록 확인 가능 (북마크 아이디어는 북마크 페이지에서 확인)
    @Override
    public IdeaResDto.IdeaCursorRes getIdeas(Long cursorId,
                                             LocalDateTime cursorTime,
                                             CustomUserDetails loginMember) {

        Channel channel = channelRepository.findByMember(loginMember.getMember())
                .orElseThrow(() -> new IdeaHandler(_CHANNEL_NOT_FOUND));

        LocalDateTime loginAt = convertToLocalDateTime(loginMember.getLoginTime());
        Pageable page = PageRequest.of(0, IDEA_CURSOR_SIZE);

        List<Idea> ideas = (cursorId == null || cursorTime == null)
                ? ideaRepository.findByIdeaFirstCursor(loginAt, channel.getId(), page)
                : ideaRepository.findByIdeaAfterCursor(loginAt, channel.getId(), cursorId, cursorTime, page);

        boolean hasNext = ideas.size() > IDEA_CURSOR_SIZE;
        if (hasNext) {
            ideas = ideas.subList(0, IDEA_CURSOR_SIZE);
        }

        return IdeaConverter.toIdeaCursor(ideas, hasNext);
    }

    // 로그인 시각 Date -> LocalDateTime 변환
    private LocalDateTime convertToLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(timeZone)
                .toLocalDateTime();
    }

    private Long countMonthlyIdeas(Member member) {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        long logCount = ideaLogRepository.countMonthlyIdeas(member.getId(), startOfMonth);
        long ideaCount = ideaRepository.countMonthlyIdeas(member.getId(), startOfMonth);
        return logCount + ideaCount;
    }
}
