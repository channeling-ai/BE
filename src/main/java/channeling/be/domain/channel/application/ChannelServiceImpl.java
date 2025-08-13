package channeling.be.domain.channel.application;

import channeling.be.domain.channel.application.model.Stats;
import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.channel.domain.repository.ChannelRepository;
import channeling.be.domain.channel.presentation.converter.ChannelConverter;
import channeling.be.domain.channel.presentation.dto.request.ChannelRequestDto;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.video.application.VideoService;
import channeling.be.global.infrastructure.redis.RedisUtil;
import channeling.be.global.infrastructure.youtube.dto.res.YoutubeChannelResDTO;
import channeling.be.global.infrastructure.youtube.YoutubeUtil;
import channeling.be.global.infrastructure.youtube.dto.model.YoutubeVideoBriefDTO;
import channeling.be.global.infrastructure.youtube.dto.model.YoutubeVideoDetailDTO;
import channeling.be.response.exception.handler.ChannelHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static channeling.be.response.code.status.ErrorStatus._CHANNEL_NOT_FOUND;
import static channeling.be.response.code.status.ErrorStatus._CHANNEL_NOT_MEMBER;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@Service
public class ChannelServiceImpl implements ChannelService {
	private final ChannelRepository channelRepository;
	private final VideoService videoService;
	private final RedisUtil redisUtil;

	@AllArgsConstructor
	@Getter
	private static class YoutubeChannelVideoData {
		YoutubeChannelResDTO.Item item;
		List<YoutubeVideoBriefDTO> briefs;
		List<YoutubeVideoDetailDTO> details;
	}

    @Override
    @Transactional
    public Channel editChannelConcept(Long channelId, ChannelRequestDto.EditChannelConceptReqDto request,Member loginMember) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelHandler(_CHANNEL_NOT_FOUND));

        if (!channel.getMember().getId().equals(loginMember.getId())) {
            throw new ChannelHandler(_CHANNEL_NOT_MEMBER);
        }
        channel.editConcept(request.getConcept()); // 더티체킹
        return channel;
    }

	@Override
	public void validateChannelByIdAndMember(Long channelId,Member member) {
		Channel channel = channelRepository.findById(channelId)
			.orElseThrow(() -> new ChannelHandler(_CHANNEL_NOT_FOUND));

		if (!channel.getMember().getId().equals(member.getId())) {
			throw new ChannelHandler(_CHANNEL_NOT_MEMBER);
		}
	}

    @Override
    @Transactional
    public Channel editChannelTarget(Long channelId, ChannelRequestDto.EditChannelTargetReqDto request, Member loginMember) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelHandler(_CHANNEL_NOT_FOUND));

        if (!channel.getMember().getId().equals(loginMember.getId())) {
            throw new ChannelHandler(_CHANNEL_NOT_MEMBER);
        }
        channel.editTarget(request.getTarget()); // 더티체킹
        return channel;
    }

	@Override
	@Transactional
	public void updateChannelVideos(Channel channel, String youtubeAccessToken) {
		// List<YoutubeVideoBriefDTO> stats = YoutubeUtil.getVideoStatistics(youtubeAccessToken, channel.getJoinDate(),
		// 	LocalDateTime.now());
		// if (stats.isEmpty()) {
		// 	log.warn("채널 {}의 비디오 통계가 없습니다.", channel.getName());
		// 	return;
		// }
	}

	@Override
	@Transactional
	public Channel updateOrCreateChannelByMember(Member member) {
		Optional<Channel> channel = channelRepository.findByMember(member);
		String googleAccessToken = redisUtil.getGoogleAccessToken(member.getId());


		// 유튜브 채널 정보 가져오기
		YoutubeChannelResDTO.Item item = YoutubeUtil.getChannelDetails(
			redisUtil.getGoogleAccessToken(member.getId()));
        long shares=YoutubeUtil.getAllVideoShares(googleAccessToken, item.getSnippet().getPublishedAt(),LocalDateTime.now());
		String playlistId = item.getContentDetails().getRelatedPlaylists().getUploads();

		//유튜브 비디오 데이터 가져오기
		YoutubeChannelVideoData data = fetchYoutubeVideoData(item, googleAccessToken, playlistId);

		//유튜브 비디오 데이터(data.details)의 각 요소의 category id 의 count를 세서 가장 높은 카테고리 추출
		String topCategoryId = getTopCategoryId(data);

		//채널이 없으면 기본 1차 저장
		Channel channelEntity = channel.orElseGet(() ->
			channelRepository.save(ChannelConverter.toNewChannel(data.item, member,shares,topCategoryId))
		);

		Stats stats=updateVideosAndAccumulateStats(data.briefs, data.details, channelEntity);
		ChannelConverter.updateChannel(channelEntity, data.item, topCategoryId,stats);
		return channelRepository.save(channelEntity);

	}

	private String getTopCategoryId(YoutubeChannelVideoData data) {
		return data.details.stream()
			.collect(Collectors.groupingBy(
				YoutubeVideoDetailDTO::getCategoryId,
				Collectors.counting()
			))
			.entrySet()
			.stream()
			.max(Map.Entry.comparingByValue())
			.map(Map.Entry::getKey)
			.orElse("0");
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

	private Stats updateVideosAndAccumulateStats(List<YoutubeVideoBriefDTO> briefs, List<YoutubeVideoDetailDTO> details, Channel channel) {
		long likeCount = 0, commentCount = 0;

		for (int i = 0; i < briefs.size(); i++) {
			YoutubeVideoBriefDTO brief = briefs.get(i);
			YoutubeVideoDetailDTO detail = details.get(i);
			likeCount += detail.getLikeCount();
			commentCount += detail.getCommentCount();
			videoService.updateVideo(brief, detail, channel);
		}
		return new Stats(likeCount, commentCount);
	}


	private YoutubeChannelVideoData fetchYoutubeVideoData(
		YoutubeChannelResDTO.Item item,
		String accessToken,
		String uploadPlaylistId
	) {
		List<YoutubeVideoBriefDTO> videoBriefs = YoutubeUtil.getVideosBriefsByPlayListId(accessToken, uploadPlaylistId);
		List<YoutubeVideoDetailDTO> videoDetails = YoutubeUtil.getVideoDetailsByIds(
			accessToken, videoBriefs.stream().map(YoutubeVideoBriefDTO::getVideoId).toList());

		return new YoutubeChannelVideoData(item, videoBriefs, videoDetails);
	}
}

