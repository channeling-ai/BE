package channeling.be.domain.report.presentation.dto;

import java.time.LocalDateTime;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.report.domain.Report;
import channeling.be.domain.video.domain.VideoCategory;

public class ReportResDTO {
	public record ReportBrief(
		Long reportId,
		String channelName,
		Long videoId,
		String videoTitle,
		String videoThumbnailUrl,
		VideoCategory videoCategory,
		Long viewCount,
		LocalDateTime uploadDate,
		LocalDateTime updatedAt
	) {
		public static ReportBrief from(Report report,  Channel channel) {
			return new ReportBrief(
				report.getId(),
				channel.getName(),
				report.getVideo().getId(),
				report.getVideo().getTitle(),
				report.getVideo().getThumbnail(),
				report.getVideo().getVideoCategory(),
				report.getVideo().getView(),
				report.getVideo().getUploadDate(),
				report.getUpdatedAt()
			);
		}
	}
}
