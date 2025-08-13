package channeling.be.domain.channel.presentation;

import java.time.LocalDateTime;
import java.util.List;

import channeling.be.domain.report.presentation.dto.ReportResDTO;
import channeling.be.domain.video.domain.VideoCategory;
import channeling.be.domain.video.presentaion.VideoResDTO;
import lombok.Builder;

public class ChannelResDTO {
	public record ChannelVideoList(
		Long channelId,
		int page,
		int size,
		boolean hasNextPage,
		long totalElements,
		int totalPages,
		List<VideoResDTO.VideoBrief> videoList
	) {
	}
	public record ChannelReportList(
		Long channelId,
		int page,
		int size,
		boolean hasNextPage,
		long totalElements,
		int totalPages,
		List<ReportResDTO.ReportBrief> reportList
	) {
	}

	@Builder
	public record PageDto (
			List<?> list,
			Integer listSize,
			Integer totalPage,
			Long totalElements,
			Boolean isFirst,
			Boolean isLast
	) {
	}

	public record ChannelInfo(
		Long channelId,
		String name,
		Long view,
		Long likeCount,
		Long subscribe,
		Long share,
		Long videoCount,
		Long comment,
		String link,
		LocalDateTime joinDate,
		String target,
		String concept,
		String image,
		VideoCategory channelHashTags,
		LocalDateTime channelUpdateAt
	) {
	}
}
