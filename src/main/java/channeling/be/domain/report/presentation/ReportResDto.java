package channeling.be.domain.report.presentation;

import channeling.be.domain.comment.domain.CommentType;
import channeling.be.domain.task.domain.TaskStatus;

import java.util.List;

public class ReportResDto {

    public record getReportAnalysisStatus(
            Long taskId,
            Long reportId,
            TaskStatus overviewStatus,
            TaskStatus analysisStatus,
            TaskStatus ideaStatus
    ) {
    }

    public record getCommentsByType(
            CommentType commentType,
            List<SingleCommentRes> commnetList
    ) {
    }
    public record SingleCommentRes(
            CommentType commentType,
            Long commentId,
            String content
    ) {
    }

    public record createReport(
            Long taskId,
            Long reportId
    ) {
    }
}
