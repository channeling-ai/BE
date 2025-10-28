package channeling.be.domain.idea.application;


import channeling.be.domain.idea.presentation.IdeaResDto;
import channeling.be.domain.member.domain.Member;

public interface IdeaService {
    IdeaResDto.ChangeIdeaBookmarkRes changeIdeaBookmark(Long ideaId, Member loginMember);

    IdeaResDto.GetBookmarkedIdeaListRes getBookmarkedIdeaList(Member loginMember, int page, int size);

    void deleteNotBookMarkedIdeas(Member loginMember);

}

