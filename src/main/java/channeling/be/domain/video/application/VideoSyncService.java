package channeling.be.domain.video.application;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.global.infrastructure.youtube.YouTubeApiService;
import channeling.be.global.infrastructure.youtube.dto.model.YoutubeVideoBriefDTO;
import channeling.be.global.infrastructure.youtube.dto.model.YoutubeVideoDetailDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoSyncService {

	private final VideoService videoService;
	private final YouTubeApiService youTubeApiService;

	/**
	 * 영상 동기화 오케스트레이터.
	 * 1) YouTube API 호출 (트랜잭션 밖)
	 * 2) DB 저장 (트랜잭션 안 — videoService는 별도 Bean이므로 proxy 동작)
	 */
	public void syncVideos(Channel channel, String googleAccessToken) {
		String playlistId = channel.getYoutubePlaylistId();

		// YouTube API 호출 — 트랜잭션 밖
		List<YoutubeVideoBriefDTO> briefs = youTubeApiService.fetchVideoBriefs(googleAccessToken, playlistId);
		List<String> videoIds = briefs.stream().map(YoutubeVideoBriefDTO::getVideoId).toList();
		List<YoutubeVideoDetailDTO> details = youTubeApiService.fetchVideoDetails(googleAccessToken, videoIds);

		// Shorts 판별 — 트랜잭션 밖 (HEAD 요청)
		youTubeApiService.markShortsVideos(briefs, details);

		// DB 저장 — 트랜잭션 안 (별도 Bean 호출로 proxy 동작)
		videoService.saveVideosWithStats(briefs, details, channel);
	}
}
