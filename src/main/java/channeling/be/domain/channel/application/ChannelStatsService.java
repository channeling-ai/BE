package channeling.be.domain.channel.application;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.channel.domain.repository.ChannelRepository;
import channeling.be.global.infrastructure.youtube.YouTubeApiService;
import channeling.be.global.infrastructure.youtube.dto.res.YoutubeChannelResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelStatsService {

	private final ChannelRepository channelRepository;
	private final YouTubeApiService youTubeApiService;
	private final TransactionTemplate transactionTemplate;

	/**
	 * 채널 통계 갱신 오케스트레이터.
	 * 1) YouTube API 호출 (트랜잭션 밖)
	 * 2) DB 저장 (트랜잭션 안)
	 */
	public void updateChannelStats(Channel channel, String googleAccessToken) {
		// YouTube API 호출 — 트랜잭션 밖
		YoutubeChannelResDTO.Item item = youTubeApiService.fetchChannelDetails(googleAccessToken);
		long shares = youTubeApiService.fetchAllVideoShares(
				googleAccessToken, item.getSnippet().getPublishedAt(), LocalDateTime.now());

		// DB 저장 — 트랜잭션 안
		transactionTemplate.executeWithoutResult(status -> {
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
		});
	}
}
