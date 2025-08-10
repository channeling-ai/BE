package channeling.be.domain.video.application;

import java.time.LocalDateTime;
import java.util.Optional;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.video.domain.Video;
import channeling.be.domain.video.domain.VideoCategory;
import channeling.be.domain.video.domain.VideoConverter;
import channeling.be.domain.video.domain.repository.VideoRepository;
import channeling.be.domain.video.presentaion.VideoResDTO;
import channeling.be.global.infrastructure.youtube.dto.model.YoutubeVideoBriefDTO;
import channeling.be.global.infrastructure.youtube.dto.model.YoutubeVideoDetailDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
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
	public Page<VideoResDTO.VideoBrief> getChannelVideoListByType(Long channelId, VideoCategory type ,int page, int size) {
		Pageable pageable = PageRequest.of(page-1, size, Sort.by(Sort.Direction.DESC, "uploadDate"));
		return videoRepository.findByChannelIdAndVideoCategoryOrderByUploadDateDesc(channelId,type ,pageable)
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
	public Video updateVideo(YoutubeVideoBriefDTO briefDTO, YoutubeVideoDetailDTO detailDTO, Channel channel) {
		Optional<Video> existing = videoRepository.findByYoutubeVideoId(briefDTO.getVideoId());

		if (existing.isPresent()) {
			Video original = existing.get();
			VideoConverter.toVideo(original, briefDTO, detailDTO);
			return videoRepository.save(original);
		} else {
			log.info("briefDTO = {}", briefDTO);
			return videoRepository.save(VideoConverter.toVideo(briefDTO,detailDTO,channel));
		}
	}
}
