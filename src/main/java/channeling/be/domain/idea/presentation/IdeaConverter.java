package channeling.be.domain.idea.presentation;


import channeling.be.domain.idea.domain.Idea;
import channeling.be.domain.idea.presentation.IdeaResDto.ChangeIdeaBookmarkRes;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class IdeaConverter {
    public static IdeaResDto.ChangeIdeaBookmarkRes toChangeIdeaBookmarkres(Long ideaId, Boolean changedBookmark) {
        return new ChangeIdeaBookmarkRes(ideaId, changedBookmark);
    }

    public static IdeaResDto.GetBookmarkedIdeaListRes toBookmarkedIdeaListRes(Page<Idea> ideaPage, int page, int size) {
        return new IdeaResDto.GetBookmarkedIdeaListRes(
                ideaPage.getTotalElements(),
                page,
                size,
                ideaPage.hasNext(),
                ideaPage.getContent().stream()
                        .map(IdeaConverter::toSingleIdeaRes)
                        .collect(Collectors.toList())

        );
    }

    public static IdeaResDto.IdeaCursorRes toIdeaCursor(List<Idea> ideas, boolean hasNext) {
        List<IdeaResDto.SingleIdeaRes> ideaRes = ideas.stream()
                .map(IdeaConverter::toSingleIdeaRes)
                .toList();

        return new IdeaResDto.IdeaCursorRes(
                ideaRes,
                ideas.isEmpty() ? null : ideas.get(ideas.size() - 1).getId(),
                ideas.isEmpty() ? null : ideas.get(ideas.size() - 1).getCreatedAt(),
                hasNext
        );

    }

    private static IdeaResDto.SingleIdeaRes toSingleIdeaRes(Idea idea) {
        return new IdeaResDto.SingleIdeaRes(idea.getId(), idea.getTitle(), idea.getContent(), idea.getHashTag(), idea.getIsBookMarked());
    }
}
