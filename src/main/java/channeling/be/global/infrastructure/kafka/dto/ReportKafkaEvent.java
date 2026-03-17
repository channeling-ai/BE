package channeling.be.global.infrastructure.kafka.dto;

public record ReportKafkaEvent(
        Long taskId,
        Long reportId,
        String googleAccessToken
) {
}
