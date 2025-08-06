package channeling.be.domain.channel.presentation;

import java.time.LocalDateTime;
import java.util.List;

import channeling.be.domain.channel.domain.ChannelHashTag;
import channeling.be.domain.report.presentation.dto.ReportResDTO;
import channeling.be.domain.video.presentaion.VideoResDTO;

public class ChannelResDTO {
	public record ChannelVideoList(
		Long channelId,
		int page,
		int size,
		boolean hasNextPage,
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
		ChannelHashTag channelHashTags,
		LocalDateTime channelUpdateAt
	) {
	}
}
