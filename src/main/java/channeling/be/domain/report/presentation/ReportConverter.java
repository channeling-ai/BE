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

    public static ReportResDto.ReportInfoDto toReportInfoRes(Report report) {
        return new ReportResDto.ReportInfoDto(
                report.getId(),
                report.getVideo().getId(),
                report.getVideo().getTitle(),
                report.getVideo().getVideoCategory(),
                report.getVideo().getUploadDate(),
                // 개요 - 영상평가
                report.getView(),
                report.getViewTopicAvg(),
                report.getViewChannelAvg(),
                report.getLikeCount(),
                report.getLikeTopicAvg(),
                report.getLikeChannelAvg(),
                report.getComment(),
                report.getCommentTopicAvg(),
                report.getCommentChannelAvg(),
                report.getConcept(),
                report.getSeo(),
                report.getRevisit(),
                // 개요 - 요약 및 댓글
                report.getSummary(),
                report.getNeutralComment(),
                report.getAdviceComment(),
                report.getPositiveComment(),
                report.getNegativeComment(),
                // 분석
                report.getLeaveAnalyze(),
                report.getOptimization()
        );
    }
}
