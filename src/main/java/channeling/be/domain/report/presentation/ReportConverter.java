package channeling.be.domain.report.presentation;

import channeling.be.domain.TrendKeyword.domain.TrendKeyword;
import channeling.be.domain.comment.domain.Comment;
import channeling.be.domain.comment.domain.CommentType;
import channeling.be.domain.idea.domain.Idea;
import channeling.be.domain.report.domain.Report;
import channeling.be.domain.task.domain.Task;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ReportConverter {
    public static ReportResDto.getReportAnalysisStatus toReportAnalysisStatus(Task task, Report report) {
        return new ReportResDto.getReportAnalysisStatus(
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

    public static ReportResDto.OverviewReport toOverview(Report report) {
        return new ReportResDto.OverviewReport(
                report.getId(),
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
                report.getNegativeComment()
        );
    }

    public static ReportResDto.AnalysisReport toAnalysis(Report report) {
        return new ReportResDto.AnalysisReport(
                report.getId(),
                report.getLeaveAnalyze(),
                report.getOptimization()
        );
    }

    public static ReportResDto.IdeaReport toIdea(Report report) {
        return new ReportResDto.IdeaReport(
                report.getId(),
                report.getVideo().getIdeas().stream()
                        .map(ReportConverter::toResIdeaInfo)
                        .sorted(Comparator.comparing(ReportResDto.IdeaInfo::isBookMarked))
                        .collect(Collectors.toList()
        ));
    }
    private static ReportResDto.IdeaInfo toResIdeaInfo(Idea idea) {
        return new ReportResDto.IdeaInfo(
                idea.getId(),
                idea.getTitle(),
                idea.getContent(),
                idea.getHashTag(),
                idea.getIsBookMarked()
        );
    }

    private static ReportResDto.TrendKeywordInfo toResTrendKeywordInfo(TrendKeyword trendKeyword) {
        return new ReportResDto.TrendKeywordInfo(
                trendKeyword.getId(),
                trendKeyword.getKeywordType(),
                trendKeyword.getKeyword(),
                trendKeyword.getScore(),
                trendKeyword.getCreatedAt()
        );
    }
}
