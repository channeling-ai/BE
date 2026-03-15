package channeling.be.domain.report.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class
ReportKafkaMessage {

    @JsonProperty("task_id")
    private Long taskId;

    @JsonProperty("report_id")
    private Long reportId;

    @JsonProperty("step")
    private String step;

    @JsonProperty("google_access_token")
    private String googleAccessToken;

    @JsonProperty("skip_vector_save")
    private boolean skipVectorSave;
}
