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

    /**
     * 북마크되지 않은 아이디어를 비동기로 삭제합니다.
     * 로그인 시 백그라운드에서 호출됩니다.
     *
     * @param loginMember 로그인한 멤버
     */
    void deleteNotBookMarkedIdeasAsync(Member loginMember);

    List<LlmResDto.CreateIdeasResDto> createIdeas(IdeaReqDto.CreateIdeaReqDto dto, Member member);

    IdeaResDto.IdeaCursorRes getIdeas(Long cursorId,
                                      LocalDateTime cursorTime,
                                      CustomUserDetails loginMember);

}

