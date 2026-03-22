package channeling.be.global.infrastructure.kafka;

import channeling.be.domain.report.domain.ReportStep;
import channeling.be.domain.task.domain.repository.TaskRepository;
import channeling.be.global.infrastructure.kafka.dto.ReportKafkaEvent;
import channeling.be.global.infrastructure.kafka.dto.ReportKafkaMessage;
import channeling.be.global.infrastructure.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
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
    private final NewTopic overviewTopic;
    private final NewTopic analysisTopic;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReportKafkaEvent(ReportKafkaEvent event) {
        try {
            kafkaTemplate.executeInTransaction(ops -> {
                ops.send(overviewTopic.name(), buildMessage(event, ReportStep.OVERVIEW));
                ops.send(analysisTopic.name(), buildMessage(event, ReportStep.ANALYSIS));
                return null;
            });
            log.info("Kafka 트랜잭션 메시지 발행 완료 - reportId: {}, taskId: {}", event.reportId(), event.taskId());
        } catch (Exception ex) {
            log.error("Kafka 트랜잭션 발행 실패 - reportId: {}, taskId: {}", event.reportId(), event.taskId(), ex);
            handleFailure(event.taskId(), ReportStep.OVERVIEW, event.userId());
            handleFailure(event.taskId(), ReportStep.ANALYSIS, event.userId());
        }
    }

    private ReportKafkaMessage buildMessage(ReportKafkaEvent event, ReportStep step) {
        return ReportKafkaMessage.builder()
                .taskId(event.taskId())
                .reportId(event.reportId())
                .step(step)
                .googleAccessToken(event.googleAccessToken())
                .skipVectorSave(true)
                .build();
    }

    private void handleFailure(Long taskId, ReportStep step, Long userId) {
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
