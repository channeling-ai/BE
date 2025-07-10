package channeling.be.domain.auth.handler;

import channeling.be.response.exception.handler.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Component
public class Oauth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper om;
    private final OAuth2AuthorizedClientService authorizedClientService;

    // 로그인 성공 시 처리하는 메서드
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // TODO [지우기] 인증 사용자 정보 샘플
        System.out.println("로그인 성공 " + authentication.getName());
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("사용자 정보 " + oAuth2User.getAttributes().get("name"));

        String jsonResponse = om.writeValueAsString(ApiResponse.onSuccess("로그인 성공"));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(jsonResponse);
    }

    // 로그인 성공 시 리프레시 토큰을 반환하는 메서드
    private String getRefreshToken(Authentication authentication) {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );

        return authorizedClient.getRefreshToken().getTokenValue();
    }
}
