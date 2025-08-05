package channeling.be.domain.report.presentation;

import channeling.be.domain.auth.annotation.LoginMember;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.report.application.ReportService;
import channeling.be.response.exception.handler.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reports")
public class ReportController implements ReportSwagger{
    private final ReportService reportService;


    @GetMapping("/status/{task-id}")
    public ApiResponse<ReportResDto.getReportAnalysisStatus> getReportAnalysisStatus(
            @PathVariable("task-id") Long taskId,
            @LoginMember Member member) {
        return ApiResponse.onSuccess(reportService.getReportAnalysisStatus(member, taskId));
    }

}
