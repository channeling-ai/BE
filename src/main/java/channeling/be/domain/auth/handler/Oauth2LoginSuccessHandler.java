package channeling.be.domain.auth.handler;

import channeling.be.domain.member.domain.Member;
import channeling.be.domain.member.domain.repository.MemberRepository;
import channeling.be.global.infrastructure.jwt.JwtUtil;
import channeling.be.global.infrastructure.redis.RedisUtil;
import channeling.be.response.exception.handler.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;



@Slf4j
@RequiredArgsConstructor
@Component
public class Oauth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper om;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final JwtUtil jwtUtil;    // JWT 토큰 생성기
    private final RedisUtil redisUtil;
    private final MemberRepository memberRepository;

    // 로그인 성공 시 처리하는 메서드
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("로그인 성공 후, 헨들러 진입");
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

        log.info("컨텍스트에서 구글 엑세스 토큰 추출 = {}" , googleAccessToken);
        /* -------------------------------------------------
         * OAuth2User 꺼내기
         * ------------------------------------------------- */
        OAuth2User oauthUser  = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attrs = oauthUser.getAttributes(); // 멤버 속성
        log.info("컨텍스트에서 토큰 내의 회원 정보 추출 = {}" , attrs);

        // TODO DB 조회해서, 실제 존재하는 사람인가? -> 있으면 기존에 있던 사람 -> 통과! + 멤버 반환, 없으면 -> 구글로그인 성공한 처음 요청 -> 회원가입 진행
//        Member member = memberService.findOrCreateMember(attrs.get()); attr에서 구글 아이디로 조회해야 할 듯...?
        Member member =  memberRepository.findByGoogleId(attrs.get("sub").toString()).get(); // 나중에 삭제해야함

        log.info("회원 구글 아이디로 db 조회 = {}" , member.getNickname());

        //멤버 아이디를 키값으로 해서 redis 에 구글 엑세스 토큰 저장
        redisUtil.saveGoogleAccessToken(member.getId(), googleAccessToken);
        log.info("멤버 아이디를 키값으로 해서 redis 에 구글 엑세스 토큰 저장 ");


        // jwt 생성 -> 우리 서버 자체 access 토큰
        String accessToken = jwtUtil.createAccessToken(member);
        log.info(" 우리 서버 자체 access 토큰 생성 = {} ",accessToken);


        // 응답 생성
        String jsonResponse = om.writeValueAsString(ApiResponse.onSuccess("로그인 성공"));
        jwtUtil.setAccessTokenHeader(response, accessToken); //헤더에 엑세스 토큰 넣기
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(jsonResponse);

    }

}
