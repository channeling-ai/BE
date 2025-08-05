package channeling.be.domain.report.application;

import channeling.be.domain.report.domain.Report;
import channeling.be.domain.report.domain.repository.ReportRepository;
import channeling.be.domain.report.presentation.dto.ReportResDTO;
import channeling.be.domain.video.domain.VideoCategory;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReportServiceImpl implements ReportService {
	private final ReportRepository reportRepository;

	@Override
	public Slice<ReportResDTO.ReportBrief> getChannelReportListByType(Long channelId, VideoCategory type, int page,
		int size) {
		Pageable pageable= PageRequest.of(page-1,size);
		Slice<Report> reports=reportRepository.findByVideoChannelIdAndVideoVideoCategoryOrderByUpdatedAtDesc(channelId,type,pageable);

		return reports.map(ReportResDTO.ReportBrief::from);

	}
}
