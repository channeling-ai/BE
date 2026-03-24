package channeling.be.domain.video.application;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.channel.domain.repository.ChannelRepository;
import channeling.be.global.infrastructure.youtube.YoutubeUtil;
import channeling.be.global.infrastructure.youtube.dto.model.YoutubeVideoBriefDTO;
import channeling.be.global.infrastructure.youtube.dto.model.YoutubeVideoDetailDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VideoSyncService {

	private final VideoService videoService;
	private final ChannelRepository channelRepository;
	private final RestTemplate restTemplate;

	private final ExecutorService shortsExecutor = Executors.newFixedThreadPool(10);

	@Transactional
	public void syncVideos(Channel channel, String googleAccessToken) {
		String playlistId = channel.getYoutubePlaylistId();

		// YouTube API N회 호출
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
			}, shortsExecutor);
			futures.add(future);
		}
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

		// Shorts 결과 반영
		shortsMap.forEach((index, isShorts) -> details.get(index).updateCategoryId("42"));

		// DB 저장
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
