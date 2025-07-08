package channeling.be.domain.video.application;


import org.springframework.data.domain.Slice;

import channeling.be.domain.video.domain.VideoCategory;
import channeling.be.domain.video.presentaion.VideoResDTO;

public interface VideoService {
	/**
	 * 채널의 비디오 목록을 조회합니다.
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
}
