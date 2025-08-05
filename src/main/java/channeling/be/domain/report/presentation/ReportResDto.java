package channeling.be.domain.report.presentation;

import channeling.be.domain.task.domain.TaskStatus;

public class ReportResDto {

    public record getReportAnalysisStatus(
            Long taskId,
            Long reportId,
            TaskStatus overviewStatus,
            TaskStatus analysisStatus,
            TaskStatus ideaStatus
    ) {
    }
}
