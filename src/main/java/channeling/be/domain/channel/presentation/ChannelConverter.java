package channeling.be.domain.channel.presentation;


import channeling.be.domain.channel.domain.Channel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import channeling.be.domain.report.presentation.dto.ReportResDTO;
import channeling.be.domain.video.presentaion.VideoResDTO;

public class ChannelConverter {
	/**
	 * 채널의 비디오 목록을 ChannelResDTO.ChannelVideoList로 변환합니다.
	 * @param channelId
	 * @param page
	 * @return ChannelResDTO.ChannelVideoList
	 */
	public static ChannelResDTO.ChannelVideoList toChannelVideoList(Long channelId, Page<VideoResDTO.VideoBrief> page) {
		return new ChannelResDTO.ChannelVideoList(
			channelId,
			page.getNumber()+1,
			page.getSize(),
			page.hasNext(),
			page.getTotalElements(),
			page.getTotalPages(),
			page.getContent()
		);
	}

	public static ChannelResDTO.ChannelReportList toChannelReportList(Long channelId, Page<ReportResDTO.ReportBrief> page){
		return new ChannelResDTO.ChannelReportList(
			channelId,
			page.getNumber()+1,
			page.getSize(),
			page.hasNext(),
			page.getTotalElements(),
			page.getTotalPages(),
			page.getContent()
		);
	}

	public static ChannelResDTO.ChannelInfo toChannelResDto(Channel channel) {
		return new ChannelResDTO.ChannelInfo(
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
