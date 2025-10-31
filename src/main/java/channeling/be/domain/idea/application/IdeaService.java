package channeling.be.domain.idea.application;


import channeling.be.domain.auth.domain.CustomUserDetails;
import channeling.be.domain.idea.presentation.IdeaReqDto;
import channeling.be.domain.idea.presentation.IdeaResDto;
import channeling.be.domain.member.domain.Member;
import channeling.be.global.infrastructure.llm.LlmResDto;

import java.time.LocalDateTime;
import java.util.List;

public interface IdeaService {
    IdeaResDto.ChangeIdeaBookmarkRes changeIdeaBookmark(Long ideaId, Member loginMember);

    IdeaResDto.GetBookmarkedIdeaListRes getBookmarkedIdeaList(Member loginMember, int page, int size);

    void deleteNotBookMarkedIdeas(Member loginMember);

    List<LlmResDto.CreateIdeasResDto> createIdeas(IdeaReqDto.CreateIdeaReqDto dto, Member member);

    IdeaResDto.IdeaCursorRes getIdeas(Long cursorId,
                                      LocalDateTime cursorTime,
                                      CustomUserDetails loginMember);

}

