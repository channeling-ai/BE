package channeling.be.domain.idea.application;

import channeling.be.domain.idea.domain.Idea;
import channeling.be.domain.idea.domain.repository.IdeaRepository;
import channeling.be.domain.idea.presentation.IdeaConverter;
import channeling.be.domain.idea.presentation.IdeaResDto;
import channeling.be.domain.member.domain.Member;
import channeling.be.response.exception.handler.IdeaHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static channeling.be.response.code.status.ErrorStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class IdeaServiceImpl implements IdeaService {
    private final IdeaRepository ideaRepository;

    @Override
    @Transactional
    public IdeaResDto.ChangeIdeaBookmarkRes changeIdeaBookmark(Long ideaId, Member loginMember) {
        //아이디어 조회
        Idea idea = ideaRepository.findWithVideoChannelMemberById(ideaId).orElseThrow(() -> new IdeaHandler(_IDEA_NOT_FOUND));
        //해당 아이디어가 로그인한 멤버의 아이디어인지 확인

        if (!idea.getVideo().getChannel().getMember().getId().equals(loginMember.getId())) {
            throw new IdeaHandler(_IDEA_NOT_MEMBER);
        }
        //아이디어 북마크 수정 -> 더티체크
        return IdeaConverter.toChangeIdeaBookmarkres(ideaId, idea.switchBookMarked());
    }

    @Override
    public IdeaResDto.GetBookmarkedIdeaListRes getBookmarkedIdeaList(Member loginMember, int page, int size) {

        Pageable pageable = PageRequest.of(page-1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Idea> ideaPage = ideaRepository.findIdeasByMemberId(loginMember.getId(), pageable);
        return IdeaConverter.toBookmarkedIdeaListRes(ideaPage, page, size);

    }
}
