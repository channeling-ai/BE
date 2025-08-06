package channeling.be.domain.report.presentation;

import channeling.be.domain.comment.domain.Comment;
import channeling.be.domain.comment.domain.CommentType;
import channeling.be.domain.report.domain.Report;
import channeling.be.domain.task.domain.Task;

import java.util.List;
import java.util.stream.Collectors;

public class ReportConverter {
    public static ReportResDto.getReportAnalysisStatus toReportAnalysisStatus(Task task, Report report) {
        return new ReportResDto.getReportAnalysisStatus(
                task.getId(),
                report.getId(),
                task.getOverviewStatus(),
                task.getAnalysisStatus(),
                task.getIdeaStatus());
    }

    public static ReportResDto.getCommentsByType toCommentsByType(CommentType commentType, List<Comment> commentList) {
        return new ReportResDto.getCommentsByType(
                commentType,
                commentList.stream()
                        .map(ReportConverter::toSingleCommentRes)
                        .collect(Collectors.toList())
        );
    }

    private static ReportResDto.SingleCommentRes toSingleCommentRes(Comment comment) {
        return new ReportResDto.SingleCommentRes(comment.getCommentType(), comment.getId(), comment.getContent());
    }
}
