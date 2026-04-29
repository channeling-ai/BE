package channeling.be.domain.auth.application;

import channeling.be.domain.member.application.MemberService;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.member.domain.MemberStatus;
import channeling.be.global.infrastructure.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberService memberService;
    private final RedisUtil redisUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
        Map<String, Object> memberAttribute = oAuth2User.getAttributes();

        log.info("OAuth2 로그인 - name: {}", memberAttribute.get("name"));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                memberAttribute,
                userNameAttributeName);
    }

    /**
     * 멤버 찾기/생성 + 탈퇴 회원 복구 + Google 토큰 저장.
     * 채널 관련 로직은 포함하지 않습니다.
     */
    @Transactional
    public LoginResult processLogin(Map<String, Object> attrs, String googleAccessToken) {
        MemberResult memberResult = memberService.findOrCreateMember(
            attrs.get("sub").toString(),
            attrs.get("email").toString(),
            attrs.get("name").toString(),
            attrs.get("picture").toString()
        );
        Member member = memberResult.member;
        redisUtil.saveGoogleAccessToken(member.getId(), googleAccessToken);

        // 탈퇴 회원 복구 처리 (30일 이내)
        if (member.getStatus().equals(MemberStatus.WITHDRAWN)
                && member.getDeletedAt() != null
                && member.getDeletedAt().isAfter(LocalDateTime.now().minusDays(30))) {
            member.restore();
            log.info("회원 복구 처리 완료: memberId={}", member.getId());
        }

        return new LoginResult(member, memberResult.isNew);
    }

    public record LoginResult(Member member, boolean isNew) {}
    public record MemberResult(Member member, boolean isNew) {}
}
