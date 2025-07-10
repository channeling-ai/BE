package channeling.be.domain.auth.handler;

import channeling.be.response.exception.handler.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Component
public class Oauth2LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper om;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String jsonResponse = om.writeValueAsString(ApiResponse.onFailure(
                HttpStatus.UNAUTHORIZED.name(),
                "로그인 실패",
                exception.getMessage()
        ));

        response.getWriter().write(jsonResponse);
    }
}
