package channeling.be.domain.idea.presentation;

import channeling.be.domain.auth.annotation.LoginMember;
import channeling.be.domain.auth.domain.CustomUserDetails;
import channeling.be.domain.idea.application.IdeaService;
import channeling.be.domain.member.domain.Member;
import channeling.be.global.infrastructure.llm.LlmResDto;
import channeling.be.response.exception.handler.ApiResponse;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ideas")
public class IdeaController implements IdeaSwagger {
    private final IdeaService ideaService;

    @Override
    @PatchMapping("/{idea-id}/bookmarks")
    public ApiResponse<IdeaResDto.ChangeIdeaBookmarkRes> changeIdeaBookmark(@PathVariable("idea-id") Long ideaId,
                                                                        @LoginMember Member loginMember) {
        return ApiResponse.onSuccess(ideaService.changeIdeaBookmark(ideaId, loginMember));
    }

    @GetMapping("/bookmarks")
    public ApiResponse<IdeaResDto.GetBookmarkedIdeaListRes> GetBookmarkedIdeaList(
                    @RequestParam(value = "page", defaultValue = "1") int page,
                    @RequestParam(value = "size", defaultValue = "6") int size,
                    @LoginMember Member loginMember) {

        return ApiResponse.onSuccess(ideaService.getBookmarkedIdeaList(loginMember,page,size));
    }

    @Override
    @PostMapping("")
    public ApiResponse<List<LlmResDto.CreateIdeasResDto>> createIdeas(
            @Valid IdeaReqDto.CreateIdeaReqDto dto,
            @LoginMember Member loginMember) {
        return ApiResponse.onSuccess(ideaService.createIdeas(dto, loginMember));
    }

    @Override
    @GetMapping("")
    public ApiResponse<IdeaResDto.IdeaCursorRes> getIdeas(
            @Nullable Long cursorId,
            @Nullable LocalDateTime cursorTime,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ApiResponse.onSuccess(ideaService.getIdeas(
                cursorId,
                cursorTime,
                customUserDetails
        ));
    }
}
