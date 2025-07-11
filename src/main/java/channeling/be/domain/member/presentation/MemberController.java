package channeling.be.domain.member.presentation;

import java.util.List;

import channeling.be.domain.member.application.MemberService;
import channeling.be.response.code.status.SuccessStatus;
import channeling.be.response.exception.handler.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/members")
public class MemberController {
	/**
	 * 멤버 관련 API를 제공하는 컨트롤러입니다.
	 */

	private final MemberService memberService;

	/**
	 * 멤버의 sns 정보를 수정하는 API입니다.
	 * @return 멤버의 기본 정보
	 */
	@PostMapping("/update-sns")
	@Operation(
		summary = "SNS 정보 수정 API",
		description = "멤버의 SNS 정보를 수정합니다. 현재는 임시로 1L로 설정된 멤버 ID를 사용합니다."
	)
	public ApiResponse<MemberResDTO.updateSnsRes> updateSns(@RequestBody MemberReqDTO.updateSnsReq updateSnsReq) {
		MemberResDTO.updateSnsRes updateSnsRes=memberService.updateSns(updateSnsReq);
		return ApiResponse.onSuccess(updateSnsRes);
	}
}
