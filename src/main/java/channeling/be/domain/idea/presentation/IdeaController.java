package channeling.be.domain.idea.presentation;

import channeling.be.domain.auth.annotation.LoginMember;
import channeling.be.domain.idea.application.IdeaService;
import channeling.be.domain.member.domain.Member;
import channeling.be.response.exception.handler.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ideas")
public class IdeaController {
    private final IdeaService ideaService;

    @PatchMapping("/bookmarks/{idea-id}")
    public ApiResponse<IdeaResDto.ChangeIdeaBookmarkRes> changeIdeaBookmark(@PathVariable("idea-id") Long ideaId,
                                                                        @LoginMember Member loginMember) {
        return ApiResponse.onSuccess(ideaService.changeIdeaBookmark(ideaId, loginMember));
    }
}
