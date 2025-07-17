package channeling.be.domain.channel.presentation;

import java.time.LocalDateTime;
import java.util.List;

import channeling.be.domain.channel.domain.ChannelHashTag;
import channeling.be.domain.video.presentaion.VideoResDTO;
import lombok.Builder;

public class ChannelResDTO {
	public record ChannelVideoList(
		Long channelId,
		int page,
		int size,
		boolean hasNextPage,
		List<VideoResDTO.VideoBrief> videoList
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
		ChannelHashTag channelHashTags,
		LocalDateTime channelUpdateAt
	) {
	}
}
