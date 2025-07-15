package channeling.be.domain.member.presentation;

import channeling.be.domain.auth.annotation.LoginMember;
import channeling.be.domain.member.domain.Member;
import channeling.be.global.infrastructure.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import channeling.be.domain.member.application.MemberService;
import channeling.be.response.code.status.SuccessStatus;
import channeling.be.response.exception.handler.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/members")
public class MemberController {

    private final RedisUtil redisUtil;
  	private final MemberService memberService;

    /**
     * 테스트용 컨트롤러 -> 나중에 삭제해 주세요!
     */

    @GetMapping("/getMemberInfo")
    public String getMemberInfo(@LoginMember Member member) {
        return String.format(
                "ID: %d, 닉네임: %s, 구글 이메일: %s, 프로필 이미지: %s, 인스타: %s, 틱톡: %s, 페이스북: %s, 트위터: %s, 구글ID: %s",
                member.getId(),
                member.getNickname(),
                member.getGoogleEmail(),
                member.getProfileImage(),
                member.getInstagramLink(),
                member.getTiktokLink(),
                member.getFacebookLink(),
                member.getTwitterLink(),
                member.getGoogleId()
        );
    }

    /**
     * 테스트용 컨트롤러 -> 나중에 삭제해 주세요!
     */
    @GetMapping("/getGoogleAceessFromRedis")
    public String getGoogleAceessFromRedis(@LoginMember Member member) {
        String googleAccessToken = redisUtil.getGoogleAccessToken(member.getId());
        log.info("googleAccessToken = {}", googleAccessToken);
        return googleAccessToken;
    }

    /**
     * 멤버 관련 API를 제공하는 컨트롤러입니다.
     */


    /**
     * 멤버의 sns 정보를 수정하는 API입니다.
     * @return 멤버의 기본 정보
     */
    @PatchMapping("/update-sns")
    @Operation(
      summary = "SNS 정보 수정 API",
      description = "멤버의 SNS 정보를 수정합니다. 현재는 임시로 1L로 설정된 멤버 ID를 사용합니다."
    )
    public ApiResponse<MemberResDTO.updateSnsRes> updateSns(@RequestBody MemberReqDTO.updateSnsReq updateSnsReq) {
      MemberResDTO.updateSnsRes updateSnsRes=memberService.updateSns(updateSnsReq);
      return ApiResponse.onSuccess(updateSnsRes);
    }

}
