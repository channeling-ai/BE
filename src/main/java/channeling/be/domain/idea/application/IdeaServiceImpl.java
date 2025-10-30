package channeling.be.domain.idea.application;

import channeling.be.domain.auth.domain.CustomUserDetails;
import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.channel.domain.repository.ChannelRepository;
import channeling.be.domain.idea.domain.Idea;
import channeling.be.domain.idea.domain.repository.IdeaRepository;
import channeling.be.domain.idea.presentation.IdeaConverter;
import channeling.be.domain.idea.presentation.IdeaReqDto;
import channeling.be.domain.idea.presentation.IdeaResDto;
import channeling.be.domain.member.domain.Member;
import channeling.be.global.infrastructure.llm.LlmResDto;
import channeling.be.global.infrastructure.llm.LlmServerUtil;
import channeling.be.response.exception.handler.IdeaHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static channeling.be.response.code.status.ErrorStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class IdeaServiceImpl implements IdeaService {

    private final IdeaRepository ideaRepository;
    private final ChannelRepository channelRepository;
    private final LlmServerUtil llmServerUtil;

    private final int IDEA_CURSOR_SIZE = 12;
    private final ZoneId timeZone = ZoneId.systemDefault(); // TODO 타임존 확인 필요

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
    public List<LlmResDto.CreateIdeasResDto> createIdeas(IdeaReqDto.CreateIdeaReqDto dto, Member member) {
        Channel channel = channelRepository.findByMember(member)
                .orElseThrow(() -> new IdeaHandler(_CHANNEL_NOT_FOUND));
        return llmServerUtil.createIdeas(dto, channel);
    }

    // 아이디어 페이지 - 채널 기반 아이디어 목록 조회
    // 로그인 시점 이후 생성된 아이디어 목록 확인 가능 (북마크 아이디어는 북마크 페이지에서 확인)
    @Override
    public IdeaResDto.IdeaCursorRes getIdeas(Long cursorId,
                                             LocalDateTime cursorTime,
                                             CustomUserDetails loginMember) {

        LocalDateTime loginAt = convertToLocalDateTime(loginMember.getLoginTime());
        Pageable page = PageRequest.of(0, IDEA_CURSOR_SIZE);

        List<Idea> ideas = (cursorId == null || cursorTime == null)
                ? ideaRepository.findByIdeaFirstCursor(loginAt, page)
                : ideaRepository.findByIdeaAfterCursor(loginAt, cursorId, cursorTime, page);

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
}
