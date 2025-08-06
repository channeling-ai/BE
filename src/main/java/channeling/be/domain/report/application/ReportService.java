package channeling.be.domain.report.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import channeling.be.domain.report.presentation.dto.ReportResDTO;
import channeling.be.domain.video.domain.VideoCategory;

public interface ReportService {

	Page<ReportResDTO.ReportBrief> getChannelReportListByType(
		Long channelId,
		VideoCategory type,
		int page,
		int size
	);
}
