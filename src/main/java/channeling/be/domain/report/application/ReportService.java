package channeling.be.domain.report.application;

import channeling.be.domain.comment.domain.CommentType;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.report.domain.Report;
import channeling.be.domain.report.presentation.ReportResDto;

import org.springframework.data.domain.Page;;

import channeling.be.domain.report.presentation.dto.ReportResDTO;
import channeling.be.domain.video.domain.VideoCategory;

public interface ReportService {

	Page<ReportResDTO.ReportBrief> getChannelReportListByType(
		Long channelId,
		VideoCategory type,
		int page,
		int size
	);
    ReportResDto.getReportAnalysisStatus getReportAnalysisStatus(Member member, Long taskId);

	Report getReportByIdAndMember(Long reportId, Member member);

	ReportResDto.getCommentsByType getCommentsByType(Report report, CommentType commentType);

    ReportResDto.createReport createReport(Member member, Long videoId);
}
