package channeling.be.domain.member.presentation;

import channeling.be.domain.auth.annotation.LoginMember;
import channeling.be.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import channeling.be.domain.member.application.MemberService;
import channeling.be.response.exception.handler.ApiResponse;
import org.springframework.web.bind.annotation.*;

import static channeling.be.domain.member.presentation.MemberReqDTO.*;
import static channeling.be.domain.member.presentation.MemberResDTO.*;


@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/members")
public class MemberController implements MemberSwagger{

  	private final MemberService memberService;
    /**
     * 멤버의 sns 정보를 수정하는 API입니다.
     * @return 멤버의 기본 정보
     */
    @Override
    @PatchMapping("/update-sns")
    public ApiResponse<updateSnsRes> updateSns(
            @LoginMember Member member,
            @RequestBody updateSnsReq updateSnsReq) {
      updateSnsRes updateSnsRes=memberService.updateSns(member,updateSnsReq);
      return ApiResponse.onSuccess(updateSnsRes);
    }

    @Override
    @PatchMapping("/profile-image")
    public ApiResponse<updateProfileImageRes> updateProfileImage(
            @LoginMember Member member,
            @ModelAttribute MemberReqDTO.ProfileImageUpdateReq updateProfileImageReq) {
        return ApiResponse.onSuccess(memberService.updateProfileImage(member,updateProfileImageReq));
    }

    @Override
    @GetMapping("")
    public ApiResponse<getMemberInfo> getMemberInfo(
        @LoginMember Member member) {
        return ApiResponse.onSuccess(memberService.getMemberInfo(member));
    }

}
