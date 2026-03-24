package channeling.be.domain.auth.handler;

import channeling.be.domain.auth.application.LoginPostProcessor;
import channeling.be.domain.auth.application.MemberOauth2UserService;
import channeling.be.domain.auth.application.MemberOauth2UserService.LoginResult;
import channeling.be.domain.channel.application.ChannelService;
import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.member.domain.Member;
import channeling.be.global.infrastructure.jwt.JwtUtil;
import channeling.be.global.infrastructure.redis.RedisUtil;
import channeling.be.response.exception.handler.YoutubeHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class Oauth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final JwtUtil jwtUtil;
    private final MemberOauth2UserService memberOauth2UserService;
    private final ChannelService channelService;
    private final RedisUtil redisUtil;
    private final LoginPostProcessor loginPostProcessor;

    @Value("${FRONT_URL:http://localhost:5173}")
    private String frontUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        long startTime = System.currentTimeMillis();
        log.info("[TIME] ========== 로그인 프로세스 시작 ==========");

        // 1. 구글 토큰 추출
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String googleAccessToken = authorizedClientService
                .loadAuthorizedClient(
                        oauthToken.getAuthorizedClientRegistrationId(),
                        oauthToken.getName())
                .getAccessToken()
                .getTokenValue();

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attrs = oauthUser.getAttributes();

        // 2. 멤버 찾기/생성 + 탈퇴 복구
        LoginResult result = memberOauth2UserService.processLogin(attrs, googleAccessToken);
        Member member = result.member();
        boolean isNew = result.isNew();

        // 3. 기본 채널 생성/조회 (동기 — YouTube API 최대 1회)
        Channel channel;
        try {
            channel = channelService.createOrGetBasicChannel(member, googleAccessToken);
        } catch (YoutubeHandler e) {
            log.warn("채널 없는 계정 로그인 시도 - error: {}", e.getCode());
            String targetUrl = UriComponentsBuilder.fromUriString(frontUrl + "/auth/callback")
                    .queryParam("token", "")
                    .queryParam("message", "Fail")
                    .queryParam("error", "NO_CHANNEL")
                    .build()
                    .toUriString();
            response.sendRedirect(targetUrl);
            return;
        }

        // 4. JWT → redirect (응답 완료)
        String accessToken = jwtUtil.createAccessToken(member);
        String targetUrl = UriComponentsBuilder.fromUriString(frontUrl + "/auth/callback")
                .queryParam("token", accessToken)
                .queryParam("message", "Success")
                .queryParam("channelId", channel.getId())
                .queryParam("isNew", isNew)
                .build()
                .toUriString();

        long totalTime = System.currentTimeMillis() - startTime;
        log.info("[TIME] ========== 로그인 프로세스 완료: 총 {}ms ==========", totalTime);

        response.sendRedirect(targetUrl);

        // 5. 비동기 후처리 (응답 이후 백그라운드)
        loginPostProcessor.executeAsync(member, channel, googleAccessToken, isNew);
    }
}
