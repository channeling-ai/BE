package channeling.be.domain.channel.presentation;


import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.video.domain.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import channeling.be.domain.video.presentaion.VideoResDTO;

import java.util.List;
import java.util.stream.Collectors;

public class ChannelConverter {
    /**
     * 채널의 비디오 목록을 ChannelResDTO.ChannelVideoList로 변환합니다.
     *
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
    public static ChannelResDTO.PageDto toVideoList(Page<Video> videoPage){

        List<VideoResDTO.VideoBrief> videoBriefs = videoPage.stream()
                .map(VideoResDTO.VideoBrief::from).collect(Collectors.toList());

        return ChannelResDTO.PageDto.builder()
                .isLast(videoPage.isLast())
                .isFirst(videoPage.isFirst())
                .totalPage(videoPage.getTotalPages())
                .totalElements(videoPage.getTotalElements())
                .listSize(videoBriefs.size())
                .list(videoBriefs)
                .build();
    }

    /**
     * Channel 객체를 ChannelResDTO.ChannelInfo로 변환합니다.
     *
     * @param channel
     * @return ChannelResDTO.ChannelInfo
     */
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
