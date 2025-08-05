package channeling.be.domain.report.presentation;

import channeling.be.domain.auth.annotation.LoginMember;
import channeling.be.domain.comment.domain.CommentType;
import channeling.be.domain.member.domain.Member;
import channeling.be.response.exception.handler.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "리포트 API", description = "리포트 관련 API입니다.")
public interface ReportSwagger{

    @Operation(summary = "리포트 분석 상태를 조회합니다.", description = "순서대로 개요, 분석, 아이디어.")
    @GetMapping("/status/{task-id}")
    ApiResponse<ReportResDto.getReportAnalysisStatus> getReportAnalysisStatus(
            @Parameter(description = "상태를 조회할 태스크 아이디 (리포트 분석 요청 시, 응답에 포함 되어 있습니다.)", example = "1")
            @PathVariable("task-id") Long taskId,
            @Parameter(hidden = true)
            @LoginMember Member member);

    @Operation(summary = "리포트의 댓글을 카테고리별로 조회합니다.", description = "상위 5개만 조회합니다.")
    @GetMapping("/{report-id}/comments")
    ApiResponse<ReportResDto.getCommentsByType> getCommentsByType(
            @Parameter(description = "댓글을 조회할 리포트 아이디", example = "1")
            @PathVariable("report-id") Long reportId,
            @Parameter(description = "댓글의 타입.")
            @RequestParam("type") CommentType commentType,
            @Parameter(hidden = true)
            @LoginMember Member member);
}
