package channeling.be.domain.member.presentation;

import channeling.be.domain.auth.annotation.LoginMember;
import channeling.be.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import channeling.be.domain.member.application.MemberService;
import channeling.be.response.exception.handler.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import static channeling.be.domain.member.presentation.MemberReqDTO.*;
import static channeling.be.domain.member.presentation.MemberResDTO.*;


@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/members")
public class MemberController {

  	private final MemberService memberService;
    /**
     * 멤버의 sns 정보를 수정하는 API입니다.
     * @return 멤버의 기본 정보
     */
    @PatchMapping("/update-sns")
    @Operation(
      summary = "SNS 정보 수정 API",
      description = "멤버의 SNS 정보를 수정합니다. 현재는 임시로 1L로 설정된 멤버 ID를 사용합니다."
    )
    public ApiResponse<updateSnsRes> updateSns(@RequestBody updateSnsReq updateSnsReq) {
      updateSnsRes updateSnsRes=memberService.updateSns(updateSnsReq);
      return ApiResponse.onSuccess(updateSnsRes);
    }

    @PatchMapping("/profile-image")
    public ApiResponse<updateProfileImageRes> updateProfileImage(
            @LoginMember Member member,
            @ModelAttribute MemberReqDTO.ProfileImageUpdateReq updateProfileImageReq) {
        return ApiResponse.onSuccess(memberService.updateProfileImage(member,updateProfileImageReq));
    }

    @GetMapping("")
    public ApiResponse<getMemberInfo> getMemberInfo(
        @LoginMember Member member) {
        return ApiResponse.onSuccess(memberService.updateProfileImage(member));
    }

}
