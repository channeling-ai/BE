package channeling.be.global.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        // ── 기본 설정 ──────────────────────────────────────────
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);           // Kafka 브로커 초기 접속 주소
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);  // Key를 String → byte[] 변환
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);  // Value를 Object → JSON byte[] 변환

        // ── acks 설정 ───────────────────────────────────────────
        config.put(ProducerConfig.ACKS_CONFIG, "all");                                   // 리더 + 모든 ISR 저장 확인 후 ack

        // ── retries / timeout 설정 ─────────────────────────────────
        // 트랜잭션 프로듀서를 사용하므로, 재시도 횟수 제거, 멱등성 기본값 true 설정
        config.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);                        // 재시도 간격 1초
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 10000);                    // send()부터 최종 ack까지 최대 10초 (빠른 실패)
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 5000);                      // 단일 전송 요청 타임아웃 5초

        // ── compression 설정 ────────────────────────────────────
        config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");                    // 배치 단위 압축 (속도/압축률 균형)

        // ── 배치/버퍼 설정 (선택) ────────────────────────────────
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);                             // 파티션당 배치 크기 16KB
        config.put(ProducerConfig.LINGER_MS_CONFIG, 5);                                  // 배치 미달 시 최대 5ms 대기 후 전송
        config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);                       // 미전송 메시지 대기 버퍼 32MB

        DefaultKafkaProducerFactory<String, Object> factory = new DefaultKafkaProducerFactory<>(config);
        factory.setTransactionIdPrefix("channeling-be-tx-");                             // 트랜잭션 프로듀서 활성화 (인스턴스별 고유 suffix 자동 부여)
        return factory;
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}