package channeling.be.domain.channel.presentation;


import channeling.be.domain.channel.domain.Channel;
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

	public static ChannelResDTO.Channel toChannelResDto(Channel channel) {
		return new ChannelResDTO.Channel(
			channel.getId(),
			channel.getName(),
			channel.getView(),
			channel.getLikeCount(),
			channel.getSubscribe(),
			channel.getShare(),
			channel.getVideoCount(),
			channel.getComment(),
			channel.getLink(),
			channel.getJoinDate(),
			channel.getTarget(),
			channel.getConcept(),
			channel.getImage(),
			channel.getChannelHashTag(),
			channel.getChannelUpdateAt()
		);
	}
}
