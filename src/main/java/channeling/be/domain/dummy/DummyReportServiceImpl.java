package channeling.be.domain.dummy;

import channeling.be.domain.comment.domain.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import channeling.be.domain.comment.domain.CommentType;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.report.application.ReportService;
import channeling.be.domain.report.domain.PageType;
import channeling.be.domain.report.domain.Report;
import channeling.be.domain.report.domain.repository.ReportRepository;
import channeling.be.domain.report.presentation.ReportConverter;
import channeling.be.domain.report.presentation.ReportResDto;
import channeling.be.domain.report.presentation.dto.ReportResDTO;
import channeling.be.domain.video.domain.VideoType;
import channeling.be.response.code.status.ErrorStatus;
import channeling.be.response.exception.handler.ReportHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class DummyReportServiceImpl implements ReportService {
	private final ReportRepository reportRepository;
	private final CommentRepository commentRepository;


	@Override
	@Transactional(readOnly = true)
	public ReportResDto.getReportAnalysisStatus getReportAnalysisStatus(Member member, Long reportId) {
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public Report getReportByIdAndMember(Long reportId, Member member) {
		return reportRepository.findById(reportId).orElseThrow(() -> new ReportHandler(ErrorStatus._REPORT_NOT_FOUND));
	}

	@Override
	@Transactional(readOnly = true)
	public ReportResDto.getCommentsByType getCommentsByType(Report report, CommentType commentType) {
		return ReportConverter.toCommentsByType(commentType, commentRepository.findTop5ByReportAndCommentType(report, commentType));
	}

	@Override
	public Report checkReport(Long reportId, PageType type, Member member) {
		// TODO 태스크 삭제하지 않는다고 가정
		return  reportRepository.findById(reportId).orElseThrow(() -> new ReportHandler(ErrorStatus._REPORT_NOT_FOUND));
	}
	@Override
	@Transactional
	public ReportResDto.deleteReport deleteReport(Member member, Long reportId) {
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ReportResDTO.ReportBrief> getChannelReportListByType(Long channelId, VideoType type, int page,
																	 int size) {
		return null;

	}

	@Override
	public ReportResDto.createReport createReport(Member member, Long videoId) {
		return null;
	}


}
