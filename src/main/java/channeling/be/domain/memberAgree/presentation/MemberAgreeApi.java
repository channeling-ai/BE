package channeling.be.domain.memberAgree.presentation;

import ch.qos.logback.core.spi.ErrorCodes;
import channeling.be.response.exception.handler.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "회원 동의 API", description = "회원 동의 관련 API입니다.")
public interface MemberAgreeApi {

    @Operation(summary = "회원 동의 수정 API", description = "회원 동의를 수정합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공입니다.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MemberAgreeResDto.Edit.class))}
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "존재하지 않는 회원 동의입니다.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorCodes.class))}
            )
    })
    ApiResponse<?> editMemberAgree(@RequestBody @Valid MemberAgreeReqDto.Edit dto);
}
