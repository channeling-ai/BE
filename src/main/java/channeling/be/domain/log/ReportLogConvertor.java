package channeling.be.domain.log;

import channeling.be.domain.report.domain.Report;

public class ReportLogConvertor {
    public static ReportLog convertToReportLog(Report report) {
        return ReportLog.builder()
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
                .build();
    }
}
