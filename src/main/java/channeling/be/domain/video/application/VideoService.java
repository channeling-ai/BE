package channeling.be.domain.video.application;


import java.time.LocalDateTime;

import org.springframework.data.domain.Slice;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.video.domain.Video;
import channeling.be.domain.video.domain.VideoCategory;
import channeling.be.domain.video.presentaion.VideoResDTO;
import channeling.be.global.infrastructure.youtube.dto.model.YoutubeVideoBriefDTO;
import channeling.be.global.infrastructure.youtube.dto.model.YoutubeVideoDetailDTO;

public interface VideoService {
	/**
	 * 채널의 비디오 목록을 페이지 기반으로 조회합니다.
	 *
	 * @param channelId 채널 ID
	 * @param type 비디오 카테고리
	 * @param page 페이지 번호
	 * @param size 페이지 크기
	 * @return 비디오 목록의 슬라이스
	 */
	Slice<VideoResDTO.VideoBrief> getChannelVideoListByType(
		Long channelId,
		VideoCategory type,
		int page,
		int size
	);

	/**
	 * 채널의 비디오 목록을 커서기반으로 조회합니다.
	 *
	 * @param channelId 채널 ID
	 * @param type 비디오 카테고리
	 * @param cursorUploadDate 페이지 번호
	 * @param size 페이지 크기
	 * @return 비디오 목록의 슬라이스
	 */
	Slice<VideoResDTO.VideoBrief> getChannelVideoListByTypeAfterCursor(
		Long channelId,
		VideoCategory type,
		LocalDateTime cursorUploadDate, // 커서
		int size
	);

	Video updateVideo(YoutubeVideoBriefDTO briefDTO, YoutubeVideoDetailDTO detailDTO,Channel channel);
}
