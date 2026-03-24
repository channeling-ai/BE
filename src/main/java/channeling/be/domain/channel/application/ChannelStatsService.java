package channeling.be.domain.channel.application;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.channel.domain.repository.ChannelRepository;
import channeling.be.global.infrastructure.youtube.YoutubeUtil;
import channeling.be.global.infrastructure.youtube.dto.res.YoutubeChannelResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChannelStatsService {

	private final ChannelRepository channelRepository;

	@Transactional
	public void updateChannelStats(Channel channel, String googleAccessToken) {
		YoutubeChannelResDTO.Item item = YoutubeUtil.getChannelDetails(googleAccessToken);
		long shares = YoutubeUtil.getAllVideoShares(
				googleAccessToken, item.getSnippet().getPublishedAt(), LocalDateTime.now());

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
}
