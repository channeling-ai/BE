package channeling.be.domain.idea.presentation;


import channeling.be.domain.idea.presentation.IdeaResDto.ChangeIdeaBookmarkRes;

public class IdeaConverter {
    public static IdeaResDto.ChangeIdeaBookmarkRes toChangeIdeaBookmarkres(Long ideaId, Boolean changedBookmark) {
        return new ChangeIdeaBookmarkRes(ideaId, changedBookmark);
    }

}
