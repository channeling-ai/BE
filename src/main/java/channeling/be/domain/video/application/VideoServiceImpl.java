package channeling.be.domain.video.application;

import channeling.be.domain.video.domain.VideoCategory;
import channeling.be.domain.video.domain.repository.VideoRepository;
import channeling.be.domain.video.presentaion.VideoResDTO;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class VideoServiceImpl implements VideoService {

	private final VideoRepository videoRepository;

	@Override
	public Slice<VideoResDTO.VideoBrief> getChannelVideoListByType(Long channelId, VideoCategory type ,int page, int size) {
		// 업로드 날짜 기준으로 내림차순 정렬하여 비디오 목록을 조회합니다.
		// TODO: 추후 커서기반 페이징 적용
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadDate"));
		return videoRepository.findByChannelIdAndVideoCategory(channelId,type ,pageable)
			.map(VideoResDTO.VideoBrief::from);
	}
}
