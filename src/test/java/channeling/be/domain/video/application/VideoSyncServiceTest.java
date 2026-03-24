package channeling.be.domain.video.application;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.channel.domain.repository.ChannelRepository;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.member.domain.MemberStatus;
import channeling.be.domain.member.domain.SubscriptionPlan;
import channeling.be.global.infrastructure.youtube.YoutubeUtil;
import channeling.be.global.infrastructure.youtube.dto.model.YoutubeVideoBriefDTO;
import channeling.be.global.infrastructure.youtube.dto.model.YoutubeVideoDetailDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VideoSyncServiceTest {

    @Mock
    private VideoService videoService;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private VideoSyncService videoSyncService;

    private MockedStatic<YoutubeUtil> youtubeUtilMock;

    @BeforeEach
    void setUp() {
        youtubeUtilMock = mockStatic(YoutubeUtil.class);
        // Shorts 판별 시 기본적으로 Shorts가 아닌 것으로 처리 (404)
        lenient().when(restTemplate.exchange(anyString(), eq(HttpMethod.HEAD), any(), eq(String.class)))
                .thenThrow(new org.springframework.web.client.HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    @AfterEach
    void tearDown() {
        youtubeUtilMock.close();
    }

    @Nested
    @DisplayName("syncVideos는")
    class Describe_syncVideos {

        @Test
        @DisplayName("영상을 조회하고 저장한다")
        void it_fetches_and_saves_videos() {
            // given
            Channel channel = createChannel(1L);
            List<YoutubeVideoBriefDTO> briefs = createBriefs();
            List<YoutubeVideoDetailDTO> details = createDetails();

            youtubeUtilMock.when(() -> YoutubeUtil.getVideosBriefsByPlayListId("token", "UU123"))
                    .thenReturn(briefs);
            youtubeUtilMock.when(() -> YoutubeUtil.getVideoDetailsByIds(eq("token"), anyList()))
                    .thenReturn(details);
            given(channelRepository.save(any(Channel.class))).willReturn(channel);

            // when
            videoSyncService.syncVideos(channel, "token");

            // then
            verify(videoService, times(2)).updateVideo(any(), any(), any());
            verify(channelRepository).save(channel);
        }

        @Test
        @DisplayName("Shorts 영상은 카테고리 42로 분류한다")
        void it_categorizes_shorts_as_42() {
            // given
            Channel channel = createChannel(1L);
            YoutubeVideoBriefDTO brief1 = new YoutubeVideoBriefDTO("vid1", "http://thumb1", "제목1", "2024-01-01T00:00:00Z");
            YoutubeVideoBriefDTO brief2 = new YoutubeVideoBriefDTO("vid2", "http://thumb2", "제목2", "2024-01-02T00:00:00Z");
            YoutubeVideoDetailDTO detail1 = new YoutubeVideoDetailDTO("설명1", "24", 1000L, 50L, 10L);
            YoutubeVideoDetailDTO detail2 = new YoutubeVideoDetailDTO("설명2", "24", 2000L, 30L, 20L);

            youtubeUtilMock.when(() -> YoutubeUtil.getVideosBriefsByPlayListId("token", "UU123"))
                    .thenReturn(List.of(brief1, brief2));
            youtubeUtilMock.when(() -> YoutubeUtil.getVideoDetailsByIds(eq("token"), anyList()))
                    .thenReturn(List.of(detail1, detail2));
            given(channelRepository.save(any(Channel.class))).willReturn(channel);

            // vid1은 Shorts (200 OK), vid2는 일반 영상 (404)
            given(restTemplate.exchange(eq("https://www.youtube.com/shorts/vid1"), eq(HttpMethod.HEAD), any(), eq(String.class)))
                    .willReturn(new ResponseEntity<>(HttpStatus.OK));
            given(restTemplate.exchange(eq("https://www.youtube.com/shorts/vid2"), eq(HttpMethod.HEAD), any(), eq(String.class)))
                    .willThrow(new org.springframework.web.client.HttpClientErrorException(HttpStatus.NOT_FOUND));

            // when
            videoSyncService.syncVideos(channel, "token");

            // then — vid1만 Shorts(42)로 분류
            assertThat(detail1.getCategoryId()).isEqualTo("42");
            assertThat(detail2.getCategoryId()).isEqualTo("24");
        }

        @Test
        @DisplayName("모든 영상의 좋아요와 댓글을 합산한다")
        void it_accumulates_likes_and_comments() {
            // given
            Channel channel = createChannel(1L);
            YoutubeVideoBriefDTO brief1 = new YoutubeVideoBriefDTO("vid1", "http://thumb1", "제목1", "2024-01-01T00:00:00Z");
            YoutubeVideoBriefDTO brief2 = new YoutubeVideoBriefDTO("vid2", "http://thumb2", "제목2", "2024-01-02T00:00:00Z");
            YoutubeVideoDetailDTO detail1 = new YoutubeVideoDetailDTO("설명1", "24", 1000L, 50L, 10L);
            YoutubeVideoDetailDTO detail2 = new YoutubeVideoDetailDTO("설명2", "24", 2000L, 30L, 20L);

            youtubeUtilMock.when(() -> YoutubeUtil.getVideosBriefsByPlayListId("token", "UU123"))
                    .thenReturn(List.of(brief1, brief2));
            youtubeUtilMock.when(() -> YoutubeUtil.getVideoDetailsByIds(eq("token"), anyList()))
                    .thenReturn(List.of(detail1, detail2));
            given(channelRepository.save(any(Channel.class))).willReturn(channel);

            // when
            videoSyncService.syncVideos(channel, "token");

            // then — likeCount = 50+30 = 80, comment = 10+20 = 30
            assertThat(channel.getLikeCount()).isEqualTo(80L);
            assertThat(channel.getComment()).isEqualTo(30L);
        }
    }

    @Nested
    @DisplayName("isYoutubeShorts는")
    class Describe_isYoutubeShorts {

        @Test
        @DisplayName("2xx 응답이면 Shorts로 판별한다")
        void it_returns_true_for_2xx() {
            given(restTemplate.exchange(anyString(), eq(HttpMethod.HEAD), any(), eq(String.class)))
                    .willReturn(new ResponseEntity<>(HttpStatus.OK));

            assertThat(videoSyncService.isYoutubeShorts("vid1")).isTrue();
        }

        @Test
        @DisplayName("404 에러면 Shorts가 아닌 것으로 판별한다")
        void it_returns_false_for_404() {
            given(restTemplate.exchange(anyString(), eq(HttpMethod.HEAD), any(), eq(String.class)))
                    .willThrow(new org.springframework.web.client.HttpClientErrorException(HttpStatus.NOT_FOUND));

            assertThat(videoSyncService.isYoutubeShorts("vid1")).isFalse();
        }
    }

    // --- Fixture ---

    private Channel createChannel(Long id) {
        Member member = Member.builder()
                .id(1L).nickname("테스트").googleEmail("test@test.com")
                .googleId("google-123").plan(SubscriptionPlan.FREE).status(MemberStatus.ACTIVE)
                .build();
        return Channel.builder()
                .id(id).member(member).youtubeChannelId("UC123").youtubePlaylistId("UU123")
                .name("테스트 채널").view(1000L).likeCount(0L).subscribe(500L).share(50L)
                .videoCount(10L).comment(0L).link("https://youtube.com/channel/UC123")
                .joinDate(LocalDateTime.of(2024, 1, 1, 0, 0)).target("default").concept("default")
                .image("http://img.url").channelUpdateAt(LocalDateTime.now())
                .build();
    }

    private List<YoutubeVideoBriefDTO> createBriefs() {
        return List.of(
                new YoutubeVideoBriefDTO("vid1", "http://thumb1", "제목1", "2024-01-01T00:00:00Z"),
                new YoutubeVideoBriefDTO("vid2", "http://thumb2", "제목2", "2024-01-02T00:00:00Z")
        );
    }

    private List<YoutubeVideoDetailDTO> createDetails() {
        return List.of(
                new YoutubeVideoDetailDTO("설명1", "24", 1000L, 50L, 10L),
                new YoutubeVideoDetailDTO("설명2", "24", 2000L, 30L, 20L)
        );
    }
}
