package channeling.be.global.infrastructure.kafka;

import channeling.be.global.infrastructure.kafka.dto.ReportKafkaEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaMessageProducer {

    private final ApplicationEventPublisher eventPublisher;

    public void sendReportMessagesAfterCommit(Long taskId, Long reportId, String googleAccessToken) {
        eventPublisher.publishEvent(new ReportKafkaEvent(taskId, reportId, googleAccessToken));
    }
}
