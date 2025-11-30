package channeling.be.domain.auth.application;

import channeling.be.domain.channel.application.ChannelService;
import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.channel.domain.repository.ChannelRepository;
import channeling.be.domain.member.application.MemberService;
import channeling.be.domain.member.domain.Member;
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

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberService memberService;
    private final ChannelService channelService;
    private final ChannelRepository channelRepository;
    private final RedisUtil redisUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 로그인 진행 시 키값 (sub)
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // oauth user 정보
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
        Map<String, Object> memberAttribute = oAuth2User.getAttributes();

        // TODO [지우기] 인증 사용자 정보 샘플
        System.out.println(" 엑세스 토큰 " + userRequest.getAccessToken().getTokenValue());
        System.out.println(" 유저 정보 샘플 " + memberAttribute);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                memberAttribute,
                userNameAttributeName);
    }

    @Transactional
    public LoginResult executeGoogleLogin(Map<String, Object> attrs, String googleAccessToken) {
        MemberResult memberResult = memberService.findOrCreateMember(
            attrs.get("sub").toString(),
            attrs.get("email").toString(),
            attrs.get("name").toString(),
            attrs.get("picture").toString()

        );
        Member member = memberResult.member;
        redisUtil.saveGoogleAccessToken(member.getId(), googleAccessToken);

        Channel channel = channelService.updateOrCreateChannelByMember(member);

        return new LoginResult(member, channel, memberResult.isNew);
    }

    /**
     * 빠른 로그인 처리 - 기존 사용자는 채널 동기화 없이 즉시 반환.
     * 채널 동기화는 비동기로 별도 처리됩니다.
     *
     * @param attrs Google OAuth2 사용자 속성
     * @param googleAccessToken Google Access Token
     * @return 로그인 결과 (기존 사용자: isNew=false, 신규 사용자: isNew=true)
     */
    @Transactional
    public LoginResult executeGoogleLoginFast(Map<String, Object> attrs, String googleAccessToken) {
        MemberResult memberResult = memberService.findOrCreateMember(
            attrs.get("sub").toString(),
            attrs.get("email").toString(),
            attrs.get("name").toString(),
            attrs.get("picture").toString()
        );
        Member member = memberResult.member;
        redisUtil.saveGoogleAccessToken(member.getId(), googleAccessToken);

        // 기존 채널 조회
        Optional<Channel> channelOpt = channelRepository.findByMember(member);

        if (channelOpt.isPresent()) {
            // 기존 사용자: 기존 채널 반환 (비동기로 업데이트 예정)
            log.info("기존 사용자 빠른 로그인 - memberId: {}", member.getId());
            return new LoginResult(member, channelOpt.get(), false);
        } else {
            // 신규 사용자: 동기로 채널 생성 (YouTube API 호출 필수)
            log.info("신규 사용자 채널 생성 시작 - memberId: {}", member.getId());
            Channel channel = channelService.updateOrCreateChannelByMember(member);
            return new LoginResult(member, channel, true);
        }
    }

    public record LoginResult(Member member, Channel channel, boolean isNew) {}
    public record MemberResult(Member member, boolean isNew) {}

}
