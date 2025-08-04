package channeling.be.domain.idea.presentation;

import channeling.be.domain.auth.annotation.LoginMember;
import channeling.be.domain.member.domain.Member;
import channeling.be.response.exception.handler.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "아이디어 API", description = "아이디어 관련 API입니다.")
public interface IdeaSwagger{

    @Operation(summary = "아이디어 북마크 추가 혹은 제거", description = "아이디어를 북마크 혹은 북마크 제거를 합니다.")
    @PatchMapping("/bookmarks/{idea-id}")
    ApiResponse<IdeaResDto.ChangeIdeaBookmarkRes> changeIdeaBookmark(
            @Parameter(description = "북마크 변경 요청할 아이디어 아이디 (북마크한 아이디어 리스트 조회 시, 응답에 포함 되어 있습니다.)", example = "1")
            @PathVariable("idea-id") Long ideaId,
            @Parameter(hidden = true)
            @LoginMember Member loginMember);
}
