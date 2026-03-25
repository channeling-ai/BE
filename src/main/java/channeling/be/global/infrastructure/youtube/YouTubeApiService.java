package channeling.be.global.infrastructure.youtube;

import channeling.be.global.infrastructure.youtube.dto.model.YoutubeVideoBriefDTO;
import channeling.be.global.infrastructure.youtube.dto.model.YoutubeVideoDetailDTO;
import channeling.be.global.infrastructure.youtube.dto.res.YoutubeChannelResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * YouTube API 호출 전담 서비스.
 * 트랜잭션 없이 순수 외부 API 호출만 수행한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class YouTubeApiService {

	private final RestTemplate restTemplate;
	private final ExecutorService shortsCheckExecutor;

	public YoutubeChannelResDTO.Item fetchChannelDetails(String accessToken) {
		return YoutubeUtil.getChannelDetails(accessToken);
	}

	public long fetchAllVideoShares(String accessToken, LocalDateTime start, LocalDateTime end) {
		return YoutubeUtil.getAllVideoShares(accessToken, start, end);
	}

	public List<YoutubeVideoBriefDTO> fetchVideoBriefs(String accessToken, String playlistId) {
		return YoutubeUtil.getVideosBriefsByPlayListId(accessToken, playlistId);
	}

	public List<YoutubeVideoDetailDTO> fetchVideoDetails(String accessToken, List<String> videoIds) {
		return YoutubeUtil.getVideoDetailsByIds(accessToken, videoIds);
	}

	/**
	 * 비디오 목록에 대해 Shorts 여부를 병렬 판별하고, Shorts인 항목의 categoryId를 "42"로 업데이트한다.
	 */
	public void markShortsVideos(List<YoutubeVideoBriefDTO> briefs, List<YoutubeVideoDetailDTO> details) {
		List<CompletableFuture<Void>> futures = new ArrayList<>();

		for (int i = 0; i < briefs.size(); i++) {
			final int index = i;
			String videoId = briefs.get(index).getVideoId();

			futures.add(CompletableFuture.runAsync(() -> {
				if (isYoutubeShorts(videoId)) {
					details.get(index).updateCategoryId("42");
				}
			}, shortsCheckExecutor));
		}
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
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

			if (response.getStatusCode().is2xxSuccessful()) {
				return true;
			}

			if (response.getStatusCode().is3xxRedirection()) {
				String location = response.getHeaders().getFirst("Location");
				return location == null || !location.contains("/watch?v=");
			}

			return false;

		} catch (HttpClientErrorException e) {
			return false;
		}
	}
}
