package channeling.be.domain.memberAgree.presentation;

import channeling.be.domain.memberAgree.application.MemberAgreeService;
import channeling.be.response.exception.handler.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원 동의 API", description = "회원 동의 관련 API입니다.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/member-agree")
public class MemberAgreeController {

    private final MemberAgreeService memberAgreeService;

    @PatchMapping("/{member-agree-id}")
    public ApiResponse<?> editMemberAgree(@RequestBody MemberAgreeDto.EditReqDto dto) {
        // TODO @AuthenticationPrincipal 추가
        return ApiResponse.onSuccess(
            MemberAgreeConverter.toEditMemberAgreeResDto(
                memberAgreeService.editMemberAgree(dto)
            )
        );
    }
}
