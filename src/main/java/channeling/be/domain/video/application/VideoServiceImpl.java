package channeling.be.domain.video.application;

import java.time.LocalDateTime;
import java.util.Optional;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.video.domain.Video;
import channeling.be.domain.video.domain.VideoCategory;
import channeling.be.domain.video.domain.VideoConverter;
import channeling.be.domain.video.domain.repository.VideoRepository;
import channeling.be.domain.video.presentaion.VideoResDTO;
import channeling.be.global.infrastructure.youtube.YoutubeVideoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class VideoServiceImpl implements VideoService {

	private final VideoRepository videoRepository;

	@Override
	public Slice<VideoResDTO.VideoBrief> getChannelVideoListByType(Long channelId, VideoCategory type ,int page, int size) {
		// 업로드 날짜 기준으로 내림차순 정렬하여 비디오 목록을 조회합니다.
		// TODO: 추후 커서기반 페이징 적용
		Pageable pageable = PageRequest.of(page-1, size, Sort.by(Sort.Direction.DESC, "uploadDate"));
		return videoRepository.findByChannelIdAndVideoCategory(channelId,type ,pageable)
			.map(VideoResDTO.VideoBrief::from);
	}

	@Override
	public Slice<VideoResDTO.VideoBrief> getChannelVideoListByTypeAfterCursor(Long channelId, VideoCategory type,
		LocalDateTime cursor, int size) {
		Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "uploadDate"));

		Slice<Video> videos= (cursor==null)
			? videoRepository.findByChannelIdAndVideoCategoryOrderByUploadDateDesc(channelId,type,pageable)
			: videoRepository.findByChannelIdAndVideoCategoryAndUploadDateLessThanOrderByUploadDateDesc(channelId, type, cursor, pageable);

		return videos.map(VideoResDTO.VideoBrief::from);
	}

	@Transactional
	@Override
	public Video updateVideo(YoutubeVideoDTO dto, Channel channel) {
		Optional<Video> existing = videoRepository.findByYoutubeVideoId(dto.getVideoId());

		if (existing.isPresent()) {
			Video original = existing.get();
			original.setView(dto.getViewCount());
			original.setLikeCount(dto.getLikeCount());
			original.setCommentCount(dto.getCommentCount());
			original.setLink("https://www.youtube.com/watch?v="+dto.getVideoId());
			original.setThumbnail(dto.getThumbnailUrl());
			original.setTitle(dto.getTitle());
			original.setDescription(dto.getDescription());
			original.setVideoCategory(VideoCategory.LONG); // TODO: 추후 유튜브 API로부터 long, short 여부를 받아와야 함
			log.info("original = {}", original);
			return videoRepository.save(original);
		} else {
			log.info("dto = {}", dto);
			return videoRepository.save(VideoConverter.toVideo(dto,channel));
		}
	}
}
