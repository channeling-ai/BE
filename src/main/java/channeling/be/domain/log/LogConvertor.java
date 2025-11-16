package channeling.be.domain.log;

import channeling.be.domain.idea.domain.Idea;
import channeling.be.domain.report.domain.Report;
import channeling.be.domain.task.domain.Task;

import java.time.LocalDateTime;

public class LogConvertor {
    public static ReportLog convertToReportLog(Report report, Task task) {
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
}
