package channeling.be.domain.channel.application;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.channel.domain.ChannelHashTag;
import channeling.be.domain.channel.domain.repository.ChannelRepository;
import channeling.be.domain.channel.presentation.dto.request.ChannelRequestDto;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.video.application.VideoService;
import channeling.be.domain.video.domain.repository.VideoRepository;
import channeling.be.global.infrastructure.redis.RedisUtil;
import channeling.be.global.infrastructure.youtube.YoutubeChannelResDTO;
import channeling.be.global.infrastructure.youtube.YoutubeUtil;
import channeling.be.global.infrastructure.youtube.YoutubeVideoDTO;
import channeling.be.response.exception.handler.ChannelHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static channeling.be.response.code.status.ErrorStatus._CHANNEL_NOT_FOUND;
import static channeling.be.response.code.status.ErrorStatus._CHANNEL_NOT_MEMBER;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@Service
public class ChannelServiceImpl implements ChannelService {
	private final ChannelRepository channelRepository;
	private final VideoService videoService;
	private final RedisUtil redisUtil;

	@Override
	@Transactional
	public Channel editChannelConcept(Long channelId, ChannelRequestDto.EditChannelConceptReqDto request) {
		Channel channel = channelRepository.findById(channelId)
			.orElseThrow(() -> new ChannelHandler(
				_CHANNEL_NOT_FOUND)); // id로 채널 조회 -> 추후 로그인 멤버 가져온 후, 멤버로 조회하는 걸로 바궈야 할 듯..? 일대일이니까..
		channel.editConcept(request.getConcept()); // 더티체킹
		return channel;
	}

	@Override
	public void validateChannelByIdAndMember(Long channelId) {
		boolean isExist = channelRepository.existsById(channelId);
		if (!isExist) {
			throw new ChannelHandler(_CHANNEL_NOT_FOUND);
		}
		//TODO: 추후 유저 + 채널 연관 관계 확인 로직 필요
	}

	@Override
	@Transactional
	public Channel editChannelTarget(Long channelId, ChannelRequestDto.EditChannelTargetReqDto request) {
		Channel channel = channelRepository.findById(channelId)
			.orElseThrow(() -> new ChannelHandler(
				_CHANNEL_NOT_FOUND));// id로 채널 조회 -> 추후 로그인 멤버 가져온 후, 멤버로 조회하는 걸로 바궈야 할 듯..? 일대일이니까..
		channel.editTarget(request.getTarget()); // 더티체킹
		return channel;
	}

	@Override
	@Transactional
	public void updateChannelVideos(Channel channel, String youtubeAccessToken) {
		List<YoutubeVideoDTO> stats= YoutubeUtil.getVideoStatistics(youtubeAccessToken,channel.getJoinDate(),LocalDateTime.now());
		if (stats.isEmpty()) {
			log.warn("채널 {}의 비디오 통계가 없습니다.", channel.getName());
			return;
		}
	}

	@Override
	@Transactional
	public Channel findOrCreateChannelByMember(Member member) {
		Optional<Channel> channel = channelRepository.findByMember(member);
		String googleAccessToken=redisUtil.getGoogleAccessToken(member.getId());
		if (channel.isPresent()) {
			log.info("멤버 {}의 채널 정보가 이미 존재합니다.", member.getNickname());
			// 유튜브 채널 통계 정보 가져오기
			// 유튜브 채널 통계 정보 가져오기
			List<YoutubeVideoDTO> stats= YoutubeUtil.getVideoStatistics(googleAccessToken,channel.get().getJoinDate(), LocalDateTime.now());

			//id 기반으로 video저장
			//비디오가 이미 존재하면 업데이트
			stats.forEach((video) -> videoService.updateVideo(video,channel.get()));

			Long totalLikeCount = stats.stream()
				.mapToLong(YoutubeVideoDTO::getLikeCount)
				.sum();
			log.info("채널 {}의 총 좋아요 수: {}", channel.get().getName(), totalLikeCount);
			Long totalCommentCount = stats.stream()
				.mapToLong(YoutubeVideoDTO::getCommentCount)
				.sum();
			log.info("채널 {}의 총 댓글 수: {}", channel.get().getName(), totalCommentCount);

			channel.get().updateChannelStats(totalLikeCount, totalCommentCount);
			channelRepository.save(channel.get());
			return channel.get();
		} else {
			YoutubeChannelResDTO.Item item = YoutubeUtil.getChannelDetails(
				redisUtil.getGoogleAccessToken(member.getId()));
			String title = item.getSnippet().getTitle();
			String profileImageUrl = item.getSnippet().getThumbnails().getDefaultThumbnail().getUrl();
			String channelUrl = "https://www.youtube.com/" + item.getSnippet().getCustomUrl();
			String channelId = item.getId();
			String uploadPlaylistId = item.getContentDetails().getRelatedPlaylists().getUploads();
			LocalDateTime publishedAt = item.getSnippet().getPublishedAt();
			Long viewCount = item.getStatistics().getViewCount();
			Long subscriberCount = item.getStatistics().getSubscriberCount();
			Long videoCount = item.getStatistics().getVideoCount();
			log.info(
				"유튜브 채널 정보: title={}, profileImageUrl={}, channelUrl={}, publishedAt={}, viewCount={}, subscriberCount={}, videoCount={}",
				title, profileImageUrl, channelUrl, publishedAt, viewCount, subscriberCount, videoCount);

			// 유튜브 채널 통계 정보 가져오기
			List<YoutubeVideoDTO> stats= YoutubeUtil.getVideoStatistics(googleAccessToken,channel.get().getJoinDate(), LocalDateTime.now());

			//id 기반으로 video저장
			//비디오가 이미 존재하면 업데이트
			stats.forEach((video) -> videoService.updateVideo(video,channel.get()));

			Long totalLikeCount = stats.stream()
				.mapToLong(YoutubeVideoDTO::getLikeCount)
				.sum();
			log.info("채널 {}의 총 좋아요 수: {}", title, totalLikeCount);
			Long totalCommentCount = stats.stream()
				.mapToLong(YoutubeVideoDTO::getCommentCount)
				.sum();
			log.info("채널 {}의 총 댓글 수: {}", title, totalCommentCount);

			return channelRepository.save(Channel.builder()
				.name(title)
				.youtubeChannelId(channelId)
				.youtubePlaylistId(uploadPlaylistId)
				.image(profileImageUrl)
				.link(channelUrl)
				.joinDate(publishedAt)
				.view(viewCount)
				.subscribe(subscriberCount)
				.videoCount(videoCount)
				.member(member)
				.target("default target") // TODO: 타겟은 추후에 프론트에서 입력받도록 해야 함
				.likeCount(totalLikeCount)
				.channelHashTag(ChannelHashTag.CHANNEL_HASH_TAG) // TODO : 해시태그는 추후에 프론트에서 선택할 수 있도록 해야 함
				.channelUpdateAt(LocalDateTime.now())
				.concept("default concept") // TODO: 컨셉은 추후에 프론트에서 입력받도록 해야 함
				.comment(totalCommentCount)
				.share(0L) // TODO: 공유 수는 추후에 유튜브 API로 가져와야 함
				.build());
		}

	}
  @Override
  public Channel getChannel(Long channelId, Member loggedInMember) {
    Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new ChannelHandler(_CHANNEL_NOT_FOUND));

    if (!channel.getMember().getId().equals(loggedInMember.getId())) {
      throw new ChannelHandler(_CHANNEL_NOT_MEMBER);
    }

    return channel;
  }
}
