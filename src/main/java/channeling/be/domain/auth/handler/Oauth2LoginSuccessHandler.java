package channeling.be.domain.auth.handler;

import channeling.be.domain.TrendKeyword.service.TrendKeywordService;
import channeling.be.domain.auth.application.MemberOauth2UserService;
import channeling.be.domain.auth.application.MemberOauth2UserService.LoginResult;
import channeling.be.domain.channel.application.ChannelSyncService;
import channeling.be.domain.idea.application.IdeaService;
import channeling.be.global.infrastructure.jwt.JwtUtil;
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
    private final JwtUtil jwtUtil;    // JWT 토큰 생성기
    private final IdeaService ideaService;
    private final MemberOauth2UserService memberOauth2UserService;
    private final TrendKeywordService trendKeywordService;
    private final ChannelSyncService channelSyncService;
    // 프론트 콜백
    @Value("${FRONT_URL:http://localhost:5173}")
    private String frontUrl;

    // 로그인 성공 시 처리하는 메서드
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // [TIME] 로그인 시간 측정 시작
        long startTime = System.currentTimeMillis();
        log.info("[TIME] ========== 로그인 프로세스 시작 ==========");

        // TODO [지우기] 인증 사용자 정보 샘플 -> 이거는 로그로 남겨둬야할듯..?
        /* -------------------------------------------------
         * 구글 accesstoken 꺼내기
         * ------------------------------------------------- */
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String googleAccessToken = authorizedClientService
                .loadAuthorizedClient(
                        oauthToken.getAuthorizedClientRegistrationId(), // "google"
                        oauthToken.getName())                           // 현재 사용자 식별자
                .getAccessToken()
                .getTokenValue();

        long tokenExtractTime = System.currentTimeMillis();
        log.info("[TIME] 구글 토큰 추출: {}ms", tokenExtractTime - startTime);

        /* -------------------------------------------------
         * OAuth2User 꺼내기
         * ------------------------------------------------- */
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attrs = oauthUser.getAttributes(); // 멤버 속성

        /* -------------------------------------------------
         * 빠른 로그인 처리:
         * - 기존 사용자: 채널 조회만 (동기화는 비동기 처리)
         * - 신규 사용자: 채널 생성 (YouTube API 호출)
         * ------------------------------------------------- */

        long loginStartTime = System.currentTimeMillis();
        LoginResult result = memberOauth2UserService.executeGoogleLoginFast(attrs, googleAccessToken);
        long loginEndTime = System.currentTimeMillis();
        log.info("[TIME] executeGoogleLoginFast (멤버/채널 처리): {}ms | 신규사용자: {}",
                loginEndTime - loginStartTime, result.isNew());

        // 비동기 작업들 (백그라운드에서 처리)
        // 기존 사용자인 경우에만 비동기 채널 동기화
        if (!result.isNew()) {
            channelSyncService.syncChannelAsync(result.member());
            log.info("[TIME] 채널 동기화 비동기 호출 완료 (백그라운드 실행)");
        }

        // 로그인시마다 채널 키워드 업데이트 (이미 @Async)
        trendKeywordService.updateChannelTrendKeyword(result.member());

        // 북마크되지 않은 아이디어 비동기 삭제
        ideaService.deleteNotBookMarkedIdeasAsync(result.member());

        // JWT 토큰 생성
        long jwtStartTime = System.currentTimeMillis();
        String accessToken = jwtUtil.createAccessToken(result.member());
        long jwtEndTime = System.currentTimeMillis();
        log.info("[TIME] JWT 토큰 생성: {}ms", jwtEndTime - jwtStartTime);

        // 프론트 응답 생성
        String targetUrl = UriComponentsBuilder.fromUriString(frontUrl + "/auth/callback") // TODO
                .queryParam("token", accessToken)
                .queryParam("message", "Success")
                .queryParam("channelId", result.channel().getId())
                .queryParam("isNew", result.isNew())
                .build()
                .toUriString();

        long totalTime = System.currentTimeMillis() - startTime;
        log.info("[TIME] ========== 로그인 프로세스 완료: 총 {}ms ========== ", totalTime);

        response.sendRedirect(targetUrl);

    }

}
