package channeling.be.global.config;

import channeling.be.domain.member.application.MemberOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // TODO 확인 후 모든 주석을 지워주세요
        http
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 인증 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 비활성화
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
                .cors(Customizer.withDefaults()) // CORS 설정 활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 관리 정책을 Stateless로 설정
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo.userService(new MemberOauth2UserService()))
                )
                // TODO : 커스텀 필터 등록 (자체 JWT 인증/인가 필터, 예외처리 필터 등)
                // .exceptionHandling(exception -> exception.authenticationEntryPoint(new AuthenticationEntryPointImpl()))
                // .addFilterBefore(jwtAuthenticationProcessingFilter(), LogoutFilter.class)
                // TODO 여기에 인증이 필요한 엔드포인트를 추가하세요.
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
        ;

        return http.build();
    }

    // TODO : JWT 인증 필터를 구현하고 주석을 해제하세요.
//    @Bean
//    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
//        return new JwtAuthenticationProcessingFilter(jwtTokenProvider, memberRepository, redisUtil);
//    }
}
