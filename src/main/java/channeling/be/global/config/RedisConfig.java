package channeling.be.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {
    // Redis 서버 호스트 (ex: localhost, redis.mycompany.com)
    @Value("${spring.data.redis.host}")
    private String host;

    // Redis 서버 포트
    @Value("${spring.data.redis.port}")
    private int port;

    /**
     * Lettuce 기반 RedisConnectionFactory Bean.
     * 애플리케이션에서 Redis에 접근할 때  * Redis 커넥션 설정 & 템플릿 bean
     * + 커넥션을 생성·관리합니다.
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Standalone(단일 노드) 환경 설정
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new LettuceConnectionFactory(config);
    }

    /**
     * Redis 문자열 키/값 처리를 위한 StringRedisTemplate Bean 등록.
     * RedisUtil에서 이 템플릿을 주입받아 사용합니다.
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }
}