package channeling.be.domain.member.presentation;

import channeling.be.domain.auth.annotation.LoginMember;
import channeling.be.domain.member.domain.Member;
import channeling.be.global.infrastructure.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/members")
public class MemberController {
    private final RedisUtil redisUtil;

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
}
