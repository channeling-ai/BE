package channeling.be.domain.report.presentation;

import channeling.be.domain.auth.annotation.LoginMember;
import channeling.be.domain.comment.domain.CommentType;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.report.application.ReportService;
import channeling.be.domain.report.domain.Report;
import channeling.be.response.exception.handler.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reports")
public class ReportController implements ReportSwagger{
    private final ReportService reportService;

    @Override
    @GetMapping("/status/{task-id}")
    public ApiResponse<ReportResDto.getReportAnalysisStatus> getReportAnalysisStatus(
            @PathVariable("task-id") Long taskId,
            @LoginMember Member member) {
        return ApiResponse.onSuccess(reportService.getReportAnalysisStatus(member, taskId));
    }

    @Override
    @GetMapping("/{report-id}/comments")
    public ApiResponse<ReportResDto.getCommentsByType> getCommentsByType(
            @PathVariable("report-id") Long reportId,
            @RequestParam(value = "type") CommentType commentType,
            @LoginMember Member member) {
        Report report = reportService.getReportByIdAndMember(reportId, member);
        return ApiResponse.onSuccess(reportService.getCommentsByType(report, commentType));

    }
}
