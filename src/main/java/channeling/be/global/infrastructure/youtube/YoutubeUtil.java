package channeling.be.global.infrastructure.youtube;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import channeling.be.global.infrastructure.youtube.dto.res.YoutubeAnalyticsResDTO;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import channeling.be.global.infrastructure.youtube.dto.model.YoutubeVideoBriefDTO;
import channeling.be.global.infrastructure.youtube.dto.model.YoutubeVideoDetailDTO;
import channeling.be.global.infrastructure.youtube.dto.model.YoutubeVideoListResDTO;
import channeling.be.global.infrastructure.youtube.dto.res.YoutubeChannelResDTO;
import channeling.be.global.infrastructure.youtube.dto.res.YoutubePlayListResDTO;
import channeling.be.response.code.status.ErrorStatus;
import channeling.be.response.exception.handler.YoutubeHandler;
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
            log.info("[YouTube API] 채널 정보 요청 URL: {}", request.uri());
            String response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString()).body();
            log.info("Response: {}", response);
            YoutubeChannelResDTO youtubeResponse = mapper.readValue(response, YoutubeChannelResDTO.class);
            if (youtubeResponse.getItems() == null || youtubeResponse.getItems().isEmpty()) {
                log.warn("유튜브 채널이 없는 계정으로 로그인 시도");
                throw new YoutubeHandler(ErrorStatus._YOUTUBE_CHANNEL_NOT_FOUND);
            }
            return youtubeResponse.getItems().get(0); // 채널 정보가 담긴 첫 번째 아이템 반환
        } catch (YoutubeHandler e) {
            throw e;
        } catch (Exception e) {
            log.error("🚨 YouTube 채널 정보 조회 실패 - 오류: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch channel details: " + e.getMessage(), e);
        }
    }

    public static long getAllVideoShares(String accessToken, LocalDateTime start, LocalDateTime end) {
        String url = UriComponentsBuilder.fromUriString("https://youtubeanalytics.googleapis.com/v2/reports")
                .queryParam("ids", "channel==MINE")
                .queryParam("startDate", start.toLocalDate().toString())
                .queryParam("endDate", end.toLocalDate().toString())
                .queryParam("metrics", "shares")
                .build().toUriString();

        try {
            log.info("[YouTube API] Analytics 공유 수 요청 URL: {}", url);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + accessToken)
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            YoutubeAnalyticsResDTO yt = new ObjectMapper().readValue(response.body(), YoutubeAnalyticsResDTO.class);
            return yt.getRows() != null && !yt.getRows().isEmpty()
                    ? Long.parseLong(String.valueOf(yt.getRows().get(0).get(0)))
                    : 0L;
        } catch (Exception e) {
            log.error("🚨 YouTube Analytics 전체 공유 수 조회 실패 - URL: {}, 오류: {}", url, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch total shares: " + e.getMessage(), e);
        }
    }


    public static JsonNode getVideo(String videoId, String accessToken) {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        try {
            String url = UriComponentsBuilder.fromUriString("https://www.googleapis.com/youtube/v3/videos")
                    .queryParam("part", "snippet,contentDetails,statistics")
                    .queryParam("id", videoId)
                    .build()
                    .toUriString();

            log.info("[YouTube API] 비디오 단건 조회 요청 URL: {}", url);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + accessToken)
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readTree(response.body());

        } catch (Exception e) {
            log.error("🚨 YouTube 비디오 정보 조회 실패 - videoId: {}, 오류: {}", videoId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch video details: " + e.getMessage(), e);
        }
    }

    public static List<YoutubeVideoBriefDTO> getVideosBriefsByPlayListId(String accessToken, String playlistId) {
        // 유튜브 API를 호출하여 플레이리스트의 비디오 정보를 가져오는 메서드
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        List<YoutubeVideoBriefDTO> videoList = new ArrayList<>();
        JsonNode jsonResponse;
        String pageToken = null;
        try {
            do {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
                                YOUTUBE_API_BASE_URL + "/playlistItems")
                        .queryParam("part", "snippet,contentDetails")
                        .queryParam("playlistId", playlistId)
                        .queryParam("maxResults", 50);

                if (pageToken != null) {
                    builder.queryParam("pageToken", pageToken);
                }

                String url = builder.build().toUriString();
                log.info("[YouTube API] 플레이리스트 조회 요청 URL: {}", url);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + accessToken)
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                YoutubePlayListResDTO youtubeResponse = mapper.readValue(response.body(), YoutubePlayListResDTO.class);

                for (YoutubePlayListResDTO.Item item : youtubeResponse.getItems()) {
                    String videoId = item.getSnippet().getResourceId().getVideoId();
                    String title = item.getSnippet().getTitle();

//                    String thumbnailUrl = item.getSnippet().getThumbnails().getHigh().getUrl();
                    YoutubePlayListResDTO.Thumbnails thumbnails = item.getSnippet().getThumbnails();
                    String thumbnailUrl = null;

                    if (thumbnails != null) {
                        if (thumbnails.getHigh() != null) {
                            thumbnailUrl = thumbnails.getHigh().getUrl();
                        } else if (thumbnails.getMedium() != null) {
                            thumbnailUrl = thumbnails.getMedium().getUrl();
                        } else if (thumbnails.getDefaultThumbnail() != null) {
							thumbnailUrl = thumbnails.getDefaultThumbnail().getUrl();
						}else{
							//섬네일 없으면 이상영상 처리, 다음 비디오로 넘어감
							continue;
						}
                    }


                    String publishedAt = item.getSnippet().getPublishedAt();
                    videoList.add(new YoutubeVideoBriefDTO(videoId, thumbnailUrl, title, publishedAt));
                }
                pageToken = youtubeResponse.getNextPageToken();

            } while (pageToken != null);
        } catch (Exception e) {
            log.error("🚨 YouTube 플레이리스트 조회 실패 - playlistId: {}, 오류: {}", playlistId, e.getMessage(), e);
            throw new YoutubeHandler(ErrorStatus._YOUTUBE_PLAYLIST_PULLING_ERROR);
        }
        return videoList;
    }

    public static List<YoutubeVideoDetailDTO> getVideoDetailsByIds(String accessToken, List<String> videoIds) {
        // 유튜브 API를 호출하여 비디오의 상세 정보를 가져오는 메서드
        // YouTube API는 한 번에 최대 50개의 video ID만 처리 가능
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        List<YoutubeVideoDetailDTO> videoDetails = new ArrayList<>();
        int batchSize = 50;

        try {
            for (int i = 0; i < videoIds.size(); i += batchSize) {
                List<String> batch = videoIds.subList(i, Math.min(i + batchSize, videoIds.size()));
                String ids = String.join(",", batch);
                String url = UriComponentsBuilder.fromUriString(YOUTUBE_API_BASE_URL + "/videos")
                        .queryParam("part", "snippet,statistics")
                        .queryParam("id", ids)
                        .build()
                        .toUriString();

                log.info("[YouTube API] 비디오 상세정보 배치 요청 URL: {} (batch {}/{})", url, (i / batchSize) + 1, (int) Math.ceil((double) videoIds.size() / batchSize));
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + accessToken)
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                log.info("[YouTube API] 비디오 상세정보 응답 - batch {}/{}, 요청 ID: {}, 상태코드: {}", (i / batchSize) + 1, (int) Math.ceil((double) videoIds.size() / batchSize), batch, response.statusCode());
                YoutubeVideoListResDTO youtubeVideoListResDTO = mapper.readValue(response.body(), YoutubeVideoListResDTO.class);

                if (youtubeVideoListResDTO.getItems() == null) {
                    log.warn("YouTube 비디오 상세정보 응답에 items가 없음 - batch 시작 인덱스: {}, batch 크기: {}", i, batch.size());
                    continue;
                }

                for (YoutubeVideoListResDTO.Item item : youtubeVideoListResDTO.getItems()) {
                    String description = item.getSnippet().getDescription();
                    String categoryId = item.getSnippet().getCategoryId();
                    Long viewCount = item.getStatistics().getViewCount();
                    Long likeCount = item.getStatistics().getLikeCount();
                    Long commentCount = item.getStatistics().getCommentCount();

                    videoDetails.add(new YoutubeVideoDetailDTO(description, categoryId, viewCount, likeCount, commentCount));
                }
            }
            return videoDetails;
        } catch (Exception e) {
            log.error("🚨 YouTube 비디오 상세정보 조회 실패 - videoIds 개수: {}, 오류: {}", videoIds.size(), e.getMessage(), e);
            throw new RuntimeException("Failed to fetch video details: " + e.getMessage(), e);
        }
    }

}
