package channeling.be.global.config;

import channeling.be.domain.auth.MemberOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final MemberOauth2UserService memberOauth2UserService;
    private final AuthenticationSuccessHandler oAuth2LoginSuccessHandler;
    private final AuthenticationFailureHandler oauth2LoginFailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // REST API 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // TODO OAuth2 로그인 설정 (사용 확인 필요)
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo.userService(memberOauth2UserService))
                        .failureHandler(oauth2LoginFailureHandler)
                        .successHandler(oAuth2LoginSuccessHandler)
                )
                // TODO 커스텀 필터 등록 (자체 JWT 인가 필터, 예외처리 필터 등)
                // .exceptionHandling(exception -> exception.authenticationEntryPoint(new AuthenticationEntryPointImpl()))
                // .addFilterBefore(jwtAuthenticationProcessingFilter(), LogoutFilter.class)
                // TODO 엔드포인트 추가 (개발 후)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
        ;

        return http.build();
    }

    // TODO JWT 인증 필터
//    @Bean
//    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
//        return new JwtAuthenticationProcessingFilter(jwtTokenProvider, memberRepository, redisUtil);
//    }
}
