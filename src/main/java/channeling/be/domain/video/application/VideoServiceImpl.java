package channeling.be.domain.video.application;

import java.time.LocalDateTime;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.channel.domain.repository.ChannelRepository;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.video.domain.Video;
import channeling.be.domain.video.domain.VideoCategory;
import channeling.be.domain.video.domain.repository.VideoRepository;
import channeling.be.domain.video.presentaion.VideoResDTO;
import channeling.be.response.exception.handler.ChannelHandler;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static channeling.be.response.code.status.ErrorStatus._CHANNEL_NOT_FOUND;
import static channeling.be.response.code.status.ErrorStatus._CHANNEL_NOT_MEMBER;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class VideoServiceImpl implements VideoService {

	private final VideoRepository videoRepository;
	private final ChannelRepository channelRepository;

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

	@Override
	public Page<Video> getRecommendedVideos(Long channelId, Integer page, Integer size, Member loginMember) {
		Channel channel = channelRepository.findById(channelId)
				.orElseThrow(() -> new ChannelHandler(_CHANNEL_NOT_FOUND));

		if (!channel.getMember().getId().equals(loginMember.getId())) {
			throw new ChannelHandler(_CHANNEL_NOT_MEMBER);
		}

		return videoRepository.findAllRecommendationByChannel(channel.getId(), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "view")));
	}
}
