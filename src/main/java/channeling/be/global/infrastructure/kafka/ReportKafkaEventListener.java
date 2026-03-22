package channeling.be.global.infrastructure.kafka;

import channeling.be.domain.report.domain.ReportStep;
import channeling.be.domain.task.domain.repository.TaskRepository;
import channeling.be.global.infrastructure.kafka.dto.ReportKafkaEvent;
import channeling.be.global.infrastructure.kafka.dto.ReportKafkaMessage;
import channeling.be.global.infrastructure.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportKafkaEventListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TaskRepository taskRepository;
    private final RedisUtil redisUtil;

    @Value("${KAFKA_OVERVIEW_TOPIC:overview-topic-v2}")
    private String overviewTopic;

    @Value("${KAFKA_ANALYSIS_TOPIC:analysis-topic-v2}")
    private String analysisTopic;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReportKafkaEvent(ReportKafkaEvent event) {
        ReportKafkaMessage overviewMessage = ReportKafkaMessage.builder()
                .taskId(event.taskId())
                .reportId(event.reportId())
                .step(ReportStep.OVERVIEW)
                .googleAccessToken(event.googleAccessToken())
                .skipVectorSave(true)
                .build();

        ReportKafkaMessage analysisMessage = ReportKafkaMessage.builder()
                .taskId(event.taskId())
                .reportId(event.reportId())
                .step(ReportStep.ANALYSIS)
                .googleAccessToken(event.googleAccessToken())
                .skipVectorSave(true)
                .build();

        try {
            kafkaTemplate.executeInTransaction(ops -> {
                ops.send(overviewTopic, overviewMessage);
                ops.send(analysisTopic, analysisMessage);
                return null;
            });
            log.info("Kafka 트랜잭션 메시지 발행 완료 - reportId: {}, taskId: {}", event.reportId(), event.taskId());
        } catch (Exception ex) {
            log.error("Kafka 트랜잭션 발행 실패 - reportId: {}, taskId: {}", event.reportId(), event.taskId(), ex);
            handleFailure(overviewTopic, event.taskId(), ReportStep.OVERVIEW, event.userId(), ex);
            handleFailure(analysisTopic, event.taskId(), ReportStep.ANALYSIS, event.userId(), ex);
        }
    }

    private void handleFailure(String topic, Long taskId, ReportStep step, Long userId, Throwable ex) {
        log.error("Kafka 전송 실패 - topic: {}, taskId: {}, step: {}", topic, taskId, step, ex);
        try {
            switch (step) {
                case OVERVIEW -> taskRepository.failOverviewStatus(taskId);
                case ANALYSIS -> taskRepository.failAnalysisStatus(taskId);
            }
            redisUtil.publishFailure(userId, step.getValue());
        } catch (Exception e) {
            log.error("Kafka 전송 실패 후처리 중 오류 - taskId: {}, step: {}", taskId, step, e);
        }
    }
}
