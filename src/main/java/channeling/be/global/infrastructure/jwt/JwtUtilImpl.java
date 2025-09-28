package channeling.be.global.infrastructure.jwt;

import channeling.be.domain.member.domain.Member;
import channeling.be.global.infrastructure.redis.RedisUtil;
import channeling.be.response.exception.handler.JwtHandler;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;



@Transactional
@Service
@RequiredArgsConstructor
@Setter(value = AccessLevel.PRIVATE)
@Slf4j
public class JwtUtilImpl implements JwtUtil {

    private final RedisUtil redisUtil;

    /** JWT 서명에 사용할 비밀키 */
    @Value("${jwt.secret}")
    private String secret;

    /** 액세스 토큰 만료 시간 */
    @Value("${jwt.access.expiration}")
    private Long accessExpiration;

    /** HTTP 요청/응답 헤더에 사용할 액세스 토큰 키 이름 */
    @Value("${jwt.access.header}")
    private String accessHeader;

    public static String BLACKLIST_TOKEN_PREFIX = "BL_AT_";


    /** JWT subject 값 - 액세스 토큰 구분용 */
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    /** JWT 클레임 키 - 구글 아이디 */
    private static final String GOOGLE_CLAIM = "googleId";

    /** JWT 클레임 키 - 회원 ID */
    private static final String USERID_CLAIM = "id";

    /** HTTP Authorization 헤더의 토큰 앞에 붙는 접두사 */
    private static final String BEARER = "Bearer ";

    @Override
    public String createAccessToken(Member member) {
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + accessExpiration * 1000))
                .withClaim(USERID_CLAIM, member.getId())
                .withClaim(GOOGLE_CLAIM, member.getGoogleId())
                .sign(Algorithm.HMAC512(secret));
    }



    @Override
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader)).filter(
                accessToken -> accessToken.startsWith(BEARER)
        ).map(accessToken -> accessToken.replace(BEARER, ""))
                .or(() -> {
                    throw new JwtHandler("Access Token이 없거나 형식이 올바르지 않습니다.");
                });
    }


    @Override
    public Optional<String> extractGoogleId(String accessToken) {
        try {

            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secret))
                    .build()
                    .verify(accessToken)
                    .getClaim(GOOGLE_CLAIM)
                    .asString());
        } catch (Exception e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }


    @Override
    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
            return true;
        } catch (TokenExpiredException ex) {
            throw new JwtHandler("만료된 JWT 토큰입니다.");

        } catch (Exception e) {
            throw new JwtHandler("유효하지 않은 Token입니다.");

        }
    }

    @Override
    public boolean isTokenInBlackList(String accessToken) {
        // Redis에서 블랙리스트로 저장된 토큰 확인
        String blacklistToken = redisUtil.getData( BLACKLIST_TOKEN_PREFIX + accessToken);

        if (blacklistToken != null) {
            throw new JwtHandler("블랙리스트 처리 되었거나 로그아웃된 Token입니다.");
        }

        return false;
    }
}
