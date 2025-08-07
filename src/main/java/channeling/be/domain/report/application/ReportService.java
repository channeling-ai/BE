package channeling.be.domain.report.application;

import channeling.be.domain.comment.domain.CommentType;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.report.domain.Report;
import channeling.be.domain.report.presentation.ReportResDto;

public interface ReportService {
    ReportResDto.getReportAnalysisStatus getReportAnalysisStatus(Member member, Long taskId);

	Report getReportByIdAndMember(Long reportId, Member member);

	ReportResDto.getCommentsByType getCommentsByType(Report report, CommentType commentType);

	Report checkReport(Long reportId, Member member);
}
