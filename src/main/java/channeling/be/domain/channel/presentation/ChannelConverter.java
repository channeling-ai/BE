package channeling.be.domain.channel.presentation;

import java.util.List;

import org.springframework.data.domain.Slice;

import channeling.be.domain.video.presentaion.VideoResDTO;

public class ChannelConverter {
	/**
	 * 채널의 비디오 목록을 ChannelResDTO.ChannelVideoList로 변환합니다.
	 * @param channelId
	 * @param slice
	 * @return ChannelResDTO.ChannelVideoList
	 */
	public static ChannelResDTO.ChannelVideoList toChannelVideoList(Long channelId, Slice<VideoResDTO.VideoBrief> slice) {
		return new ChannelResDTO.ChannelVideoList(
			channelId,
			slice.getNumber(),
			slice.getSize(),
			slice.hasNext(),
			slice.getContent()
		);
	}
}
