package channeling.be.domain.member.presentation;

import channeling.be.domain.auth.annotation.LoginMember;
import channeling.be.domain.member.domain.Member;
import channeling.be.response.exception.handler.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원 API", description = "회원 관련 API입니다.")
public interface MemberSwagger {

    @Operation(
            summary = "SNS 정보 수정 API",
            description = "멤버의 SNS 정보를 수정합니다. 4개의 링크 모두 입력해야 합니다. (없으면 사라짐)"
    )
    @PatchMapping("/update-sns")
    ApiResponse<MemberResDTO.updateSnsRes> updateSns(
            @Parameter(hidden = true)
            @LoginMember Member member,
            @Parameter(description = "sns 링크 정보")
            @RequestBody MemberReqDTO.updateSnsReq updateSnsReq);



    @Operation(
            summary ="회원 프로필 이미지 수정 API",
            description = "멤버의 프로필 이미지 정보를 수정합니다."
    )
    @PatchMapping("/profile-images")
    ApiResponse<MemberResDTO.updateProfileImageRes> updateProfileImage(
            @Parameter(hidden = true)
            @LoginMember Member member,
            @Parameter(description = "회원 사진 정보")
            @ModelAttribute MemberReqDTO.ProfileImageUpdateReq updateProfileImageReq);


    @Operation(
            summary ="회원 정보 조회 API",
            description = "멤버의 기본 회원 정보를 조회합니다."
    )
    @GetMapping("")
    ApiResponse<MemberResDTO.getMemberInfo> getMemberInfo(
            @Parameter(hidden = true)
            @LoginMember Member member);

    @Operation(
            summary = "회원 탈퇴 API",
            description = "회원 탈퇴를 처리합니다. 탈퇴 후 30일 이내에 복구가 가능합니다."
    )
    @DeleteMapping("/withdraw")
    ApiResponse<Void> withdrawMember(
            @Parameter(hidden = true) @LoginMember Member member,
            @Parameter(hidden = true) String accessToken);
}
