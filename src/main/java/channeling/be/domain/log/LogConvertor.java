package channeling.be.domain.log;

import channeling.be.domain.comment.domain.Comment;
import channeling.be.domain.comment.domain.CommentType;
import channeling.be.domain.idea.domain.Idea;
import channeling.be.domain.log.domain.DeleteType;
import channeling.be.domain.log.domain.IdeaLog;
import channeling.be.domain.log.domain.ReportLog;
import channeling.be.domain.report.domain.Report;
import channeling.be.domain.task.domain.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class LogConvertor {
    public static ReportLog convertToReportLog(DeleteType type, Report report, Task task, List<Comment> comments) {

        return ReportLog.builder()
                .loggedAt(LocalDateTime.now())
                .reportId(report.getId())
                .videoId(report.getVideo().getId())
                .title(report.getTitle())
                .view(report.getView())
                .viewTopicAvg(report.getViewTopicAvg())
                .viewChannelAvg(report.getViewChannelAvg())
                .likeCount(report.getLikeCount())
                .likeTopicAvg(report.getLikeTopicAvg())
                .likeChannelAvg(report.getLikeChannelAvg())
                .comment(report.getComment())
                .commentTopicAvg(report.getCommentTopicAvg())
                .commentChannelAvg(report.getCommentChannelAvg())
                .concept(report.getConcept())
                .seo(report.getSeo())
                .revisit(report.getRevisit())
                .summary(report.getSummary())
                .neutralComment(report.getNeutralComment())
                .adviceComment(report.getAdviceComment())
                .positiveComment(report.getPositiveComment())
                .negativeComment(report.getNegativeComment())
                .leaveAnalyze(report.getLeaveAnalyze())
                .optimization(report.getOptimization())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                // 상태값 추가
                .overviewStatus(task.getOverviewStatus())
                .analyzeStatus(task.getAnalysisStatus())
                .deleteType(type)
                // 코멘트 저장
                .positiveCommentContent(joinComment(comments, CommentType.POSITIVE))
                .negativeCommentContent(joinComment(comments, CommentType.NEGATIVE))
                .neutralCommentContent(joinComment(comments, CommentType.NEUTRAL))
                .adviceCommentContent(joinComment(comments, CommentType.ADVICE_OPINION))
                .build();
    }

    public static IdeaLog convertToIdeaLog(Idea idea) {
        return IdeaLog.builder()
                .ideaId(idea.getId())
                .loggedAt(LocalDateTime.now())
                .channelId(idea.getChannel().getId())
                .title(idea.getTitle())
                .content(idea.getContent())
                .hashTag(idea.getHashTag())
                .isBookMarked(idea.getIsBookMarked())
                .createdAt(idea.getCreatedAt())
                .updateAt(idea.getUpdatedAt())
                .build();
    }

    private static String joinComment(List<Comment> comments, CommentType type) {
        return comments.stream()
                .filter(c -> c.getCommentType().equals(type))
                .map(Comment::getContent)
                .collect(Collectors.joining(" / "));
    }
}
