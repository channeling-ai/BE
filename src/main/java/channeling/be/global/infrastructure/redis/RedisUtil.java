package channeling.be.global.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 🔹 Redis 편의 메서드를 모아 둔 유틸리티 클래스
 *    - 문자열(String) 타입 전용 StringRedisTemplate을 주입받아 사용
 *    - 기본 CRUD + 만료시간 설정 기능 제공
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RedisUtil {
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    /** redis에 저장하는 구글 엑세스의 지속 시간 **/
    @Value("${jwt.google.access.expiration}")
    private Long googleAccessExpiration;

    /** JWT 액세스 토큰 만료 시간 (초) */
    @Value("${jwt.access.expiration}")
    private Long accessExpiration;

    /** Redis에 저장할 구글 액세스 토큰 키 접두사 */
    private static final String GOOGLE_ACCESS_TOKEN_PREFIX = "GOOGLE_AT_";

    /** Redis에 저장할 블랙리스트 키 접두사 */
    public static final String BLACKLIST_TOKEN_PREFIX = "BL_";


    /**
     *  key로부터 value 조회
     *  @return 값이 없으면 null
     *  */
    public String getData(String key) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    /**
     *  해당 key 존재 여부 확인
     *  @return 존재하면 true, 없으면 false
     *  */
    public boolean existData(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }


    /**
     *  만료시간 없이 key‑value 저장
     *  */
    public void setData(String key, String value) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.set(key, value);
    }


    /**
     *  만료시간(duration 초)과 함께 key‑value 저장
     *  @param duration 초 단위 TTL(Time To Live)
     *  */
    public void setDataExpire(String key, String value, long duration) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    /**
     *  key 삭제
     *  */
    public void deleteData(String key) {
        stringRedisTemplate.delete(key);
    }


    /**
     * 멤버 ID를 키로 하여 구글 액세스 토큰을 Redis에 만료시간과 함께 저장합니다.
     *
     * @param memberId 멤버의 고유 ID
     * @param googleAccessToken 구글 액세스 토큰
     */
    public void saveGoogleAccessToken(Long memberId, String googleAccessToken) {
        String key = GOOGLE_ACCESS_TOKEN_PREFIX + memberId;
        stringRedisTemplate.opsForValue().set(key, googleAccessToken, Duration.ofSeconds(googleAccessExpiration)); // 덮어씌우기
    }

    /**
     * 멤버 ID로 저장된 구글 액세스 토큰을 조회합니다.
     *
     * @param memberId 멤버의 고유 ID
     * @return 저장된 구글 액세스 토큰 (없으면 null)
     */
    public String getGoogleAccessToken(Long memberId) {
        String key = GOOGLE_ACCESS_TOKEN_PREFIX + memberId;
        return stringRedisTemplate.opsForValue().get(key);
    }

    public Long getGoogleAccessTokenExpire(Long memberId) {
        String key = GOOGLE_ACCESS_TOKEN_PREFIX + memberId;
        // TTL (Time To Live)을 초 단위로 가져옴
        return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 원자적으로 key가 없을 때만 value를 설정하고 만료 시간을 지정 (Redis의 SET NX EX 명령을 활용)
     *
     * @param key Redis 키
     * @param value 저장할 값
     * @param duration 만료 시간 (초)
     * @return 성공 시 true, 이미 존재하면 false
     */
    public Boolean setIfAbsent(String key, String value, Long duration) {
        return stringRedisTemplate.opsForValue()
                .setIfAbsent(key, value, Duration.ofSeconds(duration));
    }

    /**
     * 멤버 ID로 저장된 구글 액세스 토큰을 삭제합니다.
     *
     * @param memberId 멤버의 고유 ID
     */
    public void deleteGoogleAccessToken(Long memberId) {
        String key = GOOGLE_ACCESS_TOKEN_PREFIX + memberId;
        stringRedisTemplate.delete(key);
    }

    /**
     * 입력받은 토큰을 블랙리스트에 넣습니다.
     *
     * @param token 블랙리스트에 넣을 토큰 값
     */
    public void addAccessTokenToBlackList(String token) {
        String key = BLACKLIST_TOKEN_PREFIX + token;
        stringRedisTemplate.opsForValue().set(
                key,
                String.valueOf(1),
                Duration.ofSeconds(accessExpiration)
        );
    }

    // ── Redis Pub/Sub ──────────────────────────────────

    private static final String COMPLETE_CHANNEL = "complete";

    public void publishFailure(Long userId, String step) {
        try {
            String innerMessage = objectMapper.writeValueAsString(
                    Map.of("status", "fail", "step", step)
            );
            String payload = objectMapper.writeValueAsString(
                    Map.of("userId", String.valueOf(userId), "message", innerMessage)
            );
            stringRedisTemplate.convertAndSend(COMPLETE_CHANNEL, payload);
            log.info("Redis 실패 알림 발행 - userId: {}, step: {}", userId, step);
        } catch (Exception e) {
            log.error("Redis Publish 실패 - userId: {}, step: {}", userId, step, e);
        }
    }
}
