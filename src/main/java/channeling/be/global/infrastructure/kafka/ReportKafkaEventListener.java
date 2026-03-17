package channeling.be.global.infrastructure.kafka;

import channeling.be.domain.report.domain.ReportStep;
import channeling.be.domain.task.domain.repository.TaskRepository;
import channeling.be.global.infrastructure.kafka.dto.ReportKafkaEvent;
import channeling.be.global.infrastructure.kafka.dto.ReportKafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportKafkaEventListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TaskRepository taskRepository;

    @Value("${KAFKA_OVERVIEW_TOPIC:overview-topic-v2}")
    private String overviewTopic;

    @Value("${KAFKA_ANALYSIS_TOPIC:analysis-topic-v2}")
    private String analysisTopic;

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

        sendWithFailureHandling(overviewTopic, overviewMessage, event.taskId(), ReportStep.OVERVIEW);
        sendWithFailureHandling(analysisTopic, analysisMessage, event.taskId(), ReportStep.ANALYSIS);
        log.info("Kafka 메시지 발행 완료 - reportId: {}, taskId: {}", event.reportId(), event.taskId());
    }

    private void sendWithFailureHandling(String topic, ReportKafkaMessage message, Long taskId, ReportStep step) {
        kafkaTemplate.send(topic, message).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Kafka 전송 실패 - topic: {}, taskId: {}, step: {}, error: {}", topic, taskId, step, ex.getMessage());
                taskRepository.findById(taskId).ifPresent(task -> {
                    switch (step) {
                        case OVERVIEW -> task.failOverview();
                        case ANALYSIS -> task.failAnalysis();
                    }
                    taskRepository.save(task);
                });
            }
        });
    }
}
