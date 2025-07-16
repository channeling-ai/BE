package channeling.be.global.infrastructure.youtube;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class YoutubeUtil {
	//HTTP 요청을
	//유튜브 API에 요청하기 위한 유틸리티 클래스

	private static final String YOUTUBE_API_BASE_URL = "https://www.googleapis.com/youtube/v3";

	public static YoutubeChannelResDTO.Item getChannelDetails(String accessToken) {
		// 유튜브 API를 호출하여 채널의 정보를 가져오는 메서드
		HttpClient client = HttpClient.newHttpClient();
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(YOUTUBE_API_BASE_URL + "/channels?part=snippet,contentDetails,statistics&mine=true"))
			.header("Authorization", "Bearer " + accessToken)
			.build();
		try {
			log.info("googleAccessToken: {}", accessToken);
			String response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString()).body();
			log.info("Response: {}", response);
			YoutubeChannelResDTO youtubeResponse = mapper.readValue(response, YoutubeChannelResDTO.class);
			return youtubeResponse.getItems().get(0); // 채널 정보가 담긴 첫 번째 아이템 반환
		} catch (Exception e) {
			throw new RuntimeException("Failed to fetch channel details: " + e.getMessage(), e);
		}
	}

	public static List<YoutubeVideoDTO> getVideoStatistics(String accessToken, LocalDateTime start,LocalDateTime end) {
		ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
		try {
			String url = UriComponentsBuilder
				.fromHttpUrl("https://youtubeanalytics.googleapis.com/v2/reports")
				.queryParam("ids", "channel==MINE")
				.queryParam("startDate", start.toLocalDate().toString())
				.queryParam("endDate", end.toLocalDate().toString())
				.queryParam("metrics", "views,likes,comments")
				.queryParam("dimensions", "video")
				.queryParam("sort", "-views")
				.queryParam("maxResults", "100")
				.build()
				.toUriString();

			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Bearer " + accessToken)
				.GET()
				.build();

			HttpResponse<String> response = HttpClient.newHttpClient()
				.send(request, HttpResponse.BodyHandlers.ofString());
			YoutubeAnalyticsResDTO ytResponse =
				objectMapper.readValue(response.body(), YoutubeAnalyticsResDTO.class);

			List<YoutubeVideoDTO> stats = new ArrayList<>();
			for (List<Object> row : ytResponse.getRows()) {
				JsonNode videoNode = getVideo(row.get(0).toString(), accessToken);
				stats.add(new YoutubeVideoDTO(
					row.get(0).toString(), // videoId
					Integer.parseInt(row.get(1).toString()), // views
					Integer.parseInt(row.get(2).toString()), // likes
					Integer.parseInt(row.get(3).toString()),  // comments
					videoNode.path("items").get(0).path("snippet").path("thumbnails").path("default").path("url").asText(), // title
					videoNode.path("items").get(0).path("snippet").path("title").asText(), // publishedAt
					videoNode.path("items").get(0).path("snippet").path("description").asText(), // description
					videoNode.path("items").get(0).path("snippet").path("publishedAt").asText() // uploadDate
				));
			}

			return stats;

		} catch (Exception e) {
			throw new RuntimeException("Failed to fetch YouTube stats: " + e.getMessage(), e);
		}
	}

	public static JsonNode getVideo(String videoId, String accessToken) {
		ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
		try {
			String url = UriComponentsBuilder
				.fromHttpUrl("https://www.googleapis.com/youtube/v3/videos")
				.queryParam("part", "snippet,contentDetails,statistics")
				.queryParam("id", videoId)
				.build()
				.toUriString();

			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", "Bearer " + accessToken)
				.GET()
				.build();

			HttpResponse<String> response = HttpClient.newHttpClient()
				.send(request, HttpResponse.BodyHandlers.ofString());
			return objectMapper.readTree(response.body());

		} catch (Exception e) {
			throw new RuntimeException("Failed to fetch video details: " + e.getMessage(), e);
		}
	}

}
