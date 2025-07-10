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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Component
public class Oauth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper om;
    private final OAuth2AuthorizedClientService authorizedClientService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // TODO 구글 리프레시 토큰, 저장 필요
        System.out.println(" 리프레시 " + getRefreshToken(authentication));

        String jsonResponse = om.writeValueAsString(ApiResponse.onSuccess("로그인 성공"));
        response.getWriter().write(jsonResponse);
    }

    private String getRefreshToken(Authentication authentication) {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );

        return authorizedClient.getRefreshToken().getTokenValue();
    }
}
