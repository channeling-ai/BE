package channeling.be.domain.report.presentation;

import channeling.be.domain.TrendKeyword.domain.TrendKeywordType;
import channeling.be.domain.comment.domain.CommentType;
import channeling.be.domain.task.domain.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public class ReportResDto {
    public record getReportAnalysisStatus(
            Long reportId,
            TaskStatus overviewStatus,
            TaskStatus analysisStatus,
            TaskStatus ideaStatus
    ) {
    }

    public record getCommentsByType(
            CommentType commentType,
            List<SingleCommentRes> commentList
    ) {
    }
    public record SingleCommentRes(
            CommentType commentType,
            Long commentId,
            String content
    ) {
    }

    public record createReport(
            Long reportId
    ) {
    }
    public record deleteReport(
            @Schema(description = "삭제된 리포트 아이디")
            Long reportId
    ) {
    }

    public record OverviewReport(
        @Schema(description = "리포트 ID (기본키)")
        Long reportId,
        @Schema(description = "개요-영상평가-조회수")
        Long view,
        @Schema(description = "개요-영상평가-조회수-동일주제평균")
        Long viewTopicAvg,
        @Schema(description = "개요-영상평가-조회수-채널평균")
        Long viewChannelAvg,
        @Schema(description = "개요-영상평가-좋아요수")
        Long likeCount,
        @Schema(description = "개요-영상평가-좋아요수-동일주제평균")
        Long likeTopicAvg,
        @Schema(description = "개요-영상평가-좋아요수-채널평균")
        Long likeChannelAvg,
        @Schema(description = "개요-영상평가-댓글수")
        Long comment,
        @Schema(description = "개요-영상평가-댓글수-동일주제평균")
        Long commentTopicAvg,
        @Schema(description = "개요-영상평가-댓글수-채널평균")
        Long commentChannelAvg,
        @Schema(description = "개요-영상평가-컨셉일관성")
        Integer concept,
        @Schema(description = "개요-영상평가-seo구성")
        Long seo,
        @Schema(description = "개요-영상평가-재방문률")
        Long revisit,

        @Schema(description = "개요-영상요약")
        String summary,

        @Schema(description = "개요-댓글반응-중립댓글수")
        Long neutralComment,
        @Schema(description = "개요-댓글반응-조언댓글수")
        Long adviceComment,
        @Schema(description = "개요-댓글반응-긍정댓글수")
        Long positiveComment,
        @Schema(description = "개요-댓글반응-부정댓글수")
        Long negativeComment

    ) {}

    public record AnalysisReport (
        @Schema(description = "리포트 ID (기본키)")
        Long reportId,
        @Schema(description = "분석-시청자이탈분석")
        String leaveAnalyze,
        @Schema(description = "분석-알고리즘최적화")
        String optimization
    ) {}

    public record IdeaReport (
        @Schema(description = "리포트 ID (기본키)")
        Long reportId,
        @Schema(description = "리포트 - 아이디어")
        List<IdeaInfo> idea,
        @Schema(description = "리포트 - 트랜드 키워드")
        List<TrendKeywordInfo> trend
    ) {}

    // 리포트 페이지 조회 응답 DTO - 아이디어 정보
    public record IdeaInfo (
        Long ideaId,
        String title,
        String content,
        String hashTag,
        Boolean isBookMarked
    ) {}
    // 리포트 페이지 조회 응답 DTO - 트렌드 키워드 정보
    public record TrendKeywordInfo(
        Long trendKeywordId,
        TrendKeywordType keywordType,
        String keyword,
        Integer score,
        LocalDateTime createdAt
    ) {}
}
