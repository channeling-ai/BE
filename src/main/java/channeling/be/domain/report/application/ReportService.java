package channeling.be.domain.report.application;

import channeling.be.domain.member.domain.Member;
import channeling.be.domain.report.presentation.ReportResDto;

public interface ReportService {
    ReportResDto.getReportAnalysisStatus getReportAnalysisStatus(Member member, Long taskId);
}
