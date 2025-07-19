package channeling.be.domain.idea.presentation;


public class IdeaResDto {

    public record ChangeIdeaBookmarkRes(
            Long ideaId,
            Boolean isBookmarked

    ) {
    }

}
