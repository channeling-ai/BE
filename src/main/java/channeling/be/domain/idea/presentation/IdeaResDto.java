package channeling.be.domain.idea.presentation;



import java.util.List;

public class IdeaResDto {

    public record ChangeIdeaBookmarkRes(
            Long ideaId,
            Boolean isBookmarked

    ) {
    }
    public record GetBookmarkedIdeaListRes(
            Long total,
            int page,
            int size,
            boolean hasNextPage,
            List<IdeaResDto.SingleIdeaRes> bookmarkedIdeaList

    ) {
    }
    public record SingleIdeaRes(
            Long ideaId,
            String title, // 제목
            String content, // 내용
            String hashTag, // 해시태그 Json 리스트
            boolean isBookmarked
    ) {
    }

}
