package channeling.be.domain.channel.application;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static channeling.be.response.code.status.ErrorStatus._CHANNEL_NOT_FOUND;
import static channeling.be.response.code.status.ErrorStatus._CHANNEL_NOT_MEMBER;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@Service
public class ChannelServiceImpl implements ChannelService {
	private final ChannelRepository channelRepository;
	private final VideoService videoService;
	private final RedisUtil redisUtil;
	private final RestTemplate restTemplate;

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

	/**
	 * 기존 호출처 호환을 위한 퍼사드.
	 * 내부적으로 3단계(채널 생성/조회 → 영상 동기화 → 통계 업데이트)를 순차 호출한다.
	 */
	@Override
	public Channel updateOrCreateChannelByMember(Member member) {
		String token = redisUtil.getGoogleAccessToken(member.getId());
		Channel channel = createOrGetBasicChannel(member, token);
		syncVideos(channel, token);
		updateChannelStats(channel, token);
		return channel;
	}

	// ─── Step 4-1: 기본 채널 생성/조회 ───

	@Override
	public Channel createOrGetBasicChannel(Member member, String googleAccessToken) {
		Optional<Channel> existing = channelRepository.findByMember(member);
		if (existing.isPresent()) {
			return existing.get();
		}

		// YouTube API 1회 호출 (트랜잭션 밖)
		YoutubeChannelResDTO.Item item = YoutubeUtil.getChannelDetails(googleAccessToken);
		long shares = YoutubeUtil.getAllVideoShares(
				googleAccessToken, item.getSnippet().getPublishedAt(), LocalDateTime.now());
		String topCategoryId = "0"; // 신규 채널은 영상 동기화 전이므로 기본값

		return saveNewChannel(item, member, shares, topCategoryId);
	}

	@Transactional
	protected Channel saveNewChannel(YoutubeChannelResDTO.Item item, Member member, long shares, String topCategoryId) {
		return channelRepository.save(ChannelConverter.toNewChannel(item, member, shares, topCategoryId));
	}

	// ─── Step 4-2: 영상 동기화 ───

	@Override
	public void syncVideos(Channel channel, String googleAccessToken) {
		String playlistId = channel.getYoutubePlaylistId();

		// YouTube API N회 호출 (트랜잭션 밖)
		List<YoutubeVideoBriefDTO> briefs = YoutubeUtil.getVideosBriefsByPlayListId(googleAccessToken, playlistId);
		List<YoutubeVideoDetailDTO> details = YoutubeUtil.getVideoDetailsByIds(
				googleAccessToken, briefs.stream().map(YoutubeVideoBriefDTO::getVideoId).toList());

		// Shorts 판별 — thread-safe 구조 (ConcurrentHashMap으로 결과 수집)
		ConcurrentHashMap<Integer, Boolean> shortsMap = new ConcurrentHashMap<>();
		List<CompletableFuture<Void>> futures = new ArrayList<>();

		for (int i = 0; i < briefs.size(); i++) {
			final int index = i;
			String videoId = briefs.get(i).getVideoId();

			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				if (isYoutubeShorts(videoId)) {
					shortsMap.put(index, true);
				}
			});
			futures.add(future);
		}
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

		// Shorts 결과 반영
		shortsMap.forEach((index, isShorts) -> details.get(index).updateCategoryId("42"));

		// @Transactional — DB 저장만
		saveVideos(briefs, details, channel);
	}

	@Transactional
	protected void saveVideos(
			List<YoutubeVideoBriefDTO> briefs,
			List<YoutubeVideoDetailDTO> details,
			Channel channel
	) {
		long likeCount = 0, commentCount = 0;

		for (int i = 0; i < briefs.size(); i++) {
			YoutubeVideoBriefDTO brief = briefs.get(i);
			YoutubeVideoDetailDTO detail = details.get(i);
			likeCount += detail.getLikeCount();
			commentCount += detail.getCommentCount();
			videoService.updateVideo(brief, detail, channel);
		}

		channel.updateChannelStats(likeCount, commentCount);
		channelRepository.save(channel);
	}

	// ─── Step 4-3: 채널 통계 업데이트 ───

	@Override
	public void updateChannelStats(Channel channel, String googleAccessToken) {
		// YouTube Analytics 호출 (트랜잭션 밖)
		YoutubeChannelResDTO.Item item = YoutubeUtil.getChannelDetails(googleAccessToken);
		long shares = YoutubeUtil.getAllVideoShares(
				googleAccessToken, item.getSnippet().getPublishedAt(), LocalDateTime.now());

		// @Transactional — DB 업데이트만
		saveChannelStats(channel, item, shares);
	}

	@Transactional
	protected void saveChannelStats(Channel channel, YoutubeChannelResDTO.Item item, long shares) {
		channel.updateChannelInfo(
				item.getSnippet().getTitle(),
				item.getId(),
				item.getContentDetails().getRelatedPlaylists().getUploads(),
				item.getSnippet().getThumbnails().getDefaultThumbnail().getUrl(),
				"https://www.youtube.com/channel/" + item.getId(),
				item.getSnippet().getPublishedAt(),
				item.getStatistics().getViewCount(),
				item.getStatistics().getSubscriberCount(),
				item.getStatistics().getVideoCount(),
				channel.getLikeCount(),
				channel.getComment(),
				channel.getChannelHashTag() != null ? channel.getChannelHashTag().getId() : "0",
				shares
		);
		channelRepository.save(channel);
	}

	// ─── 기존 메서드 ───

	@Override
	public Channel getChannel(Long channelId, Member loggedInMember) {
		Channel channel = channelRepository.findById(channelId)
			.orElseThrow(() -> new ChannelHandler(_CHANNEL_NOT_FOUND));

		if (!channel.getMember().getId().equals(loggedInMember.getId())) {
			throw new ChannelHandler(_CHANNEL_NOT_MEMBER);
		}

		return channel;
	}

	public boolean isYoutubeShorts(String videoId) {
		String shortsUrl = "https://www.youtube.com/shorts/" + videoId;

		try {
			ResponseEntity<String> response = restTemplate.exchange(
				shortsUrl,
				HttpMethod.HEAD,
				null,
				String.class
			);

			// 2xx 응답이고 리다이렉트가 없으면 Shorts
			if (response.getStatusCode().is2xxSuccessful()) {
				return true;
			}

			// 3xx 리다이렉트면 Location 확인
			if (response.getStatusCode().is3xxRedirection()) {
				String location = response.getHeaders().getFirst("Location");
				return location == null || !location.contains("/watch?v=");
			}

			return false; // 4xx, 5xx 에러

		} catch (HttpClientErrorException e) {
			//만약 404 에러일 경우 shorts 가 아니라고 판단
			return false;
		}
	}
}

