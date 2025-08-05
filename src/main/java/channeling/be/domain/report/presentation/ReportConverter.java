package channeling.be.domain.report.presentation;

import channeling.be.domain.report.domain.Report;
import channeling.be.domain.task.domain.Task;

public class ReportConverter {
    public static ReportResDto.getReportAnalysisStatus toReportAnalysisStatus(Task task, Report report) {
        return new ReportResDto.getReportAnalysisStatus(
                task.getId(),
                report.getId(),
                task.getOverviewStatus(),
                task.getAnalysisStatus(),
                task.getIdeaStatus());
    }
}
