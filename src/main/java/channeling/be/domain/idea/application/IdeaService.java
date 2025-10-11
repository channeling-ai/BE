package channeling.be.domain.idea.application;


import channeling.be.domain.idea.domain.Idea;
import channeling.be.domain.idea.presentation.IdeaReqDto;
import channeling.be.domain.idea.presentation.IdeaResDto;
import channeling.be.domain.member.domain.Member;
import channeling.be.global.infrastructure.llm.LlmResDto;

import java.util.List;

public interface IdeaService {
    IdeaResDto.ChangeIdeaBookmarkRes changeIdeaBookmark(Long ideaId, Member loginMember);

    IdeaResDto.GetBookmarkedIdeaListRes getBookmarkedIdeaList(Member loginMember, int page, int size);

    List<LlmResDto.CreateIdeasResDto> createIdeas(IdeaReqDto.CreateIdeaReqDto dto, Member member);

}

