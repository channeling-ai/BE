package channeling.be.domain.channel.presentation;

import java.util.List;

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
}
