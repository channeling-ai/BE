package channeling.be.domain.channel.application;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.channel.domain.repository.ChannelRepository;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.member.domain.MemberStatus;
import channeling.be.domain.member.domain.SubscriptionPlan;
import channeling.be.domain.video.application.VideoService;
import channeling.be.domain.video.domain.Video;
import channeling.be.global.infrastructure.redis.RedisUtil;
import channeling.be.global.infrastructure.youtube.YoutubeUtil;
import channeling.be.global.infrastructure.youtube.dto.model.YoutubeVideoBriefDTO;
import channeling.be.global.infrastructure.youtube.dto.model.YoutubeVideoDetailDTO;
import channeling.be.global.infrastructure.youtube.dto.res.YoutubeChannelResDTO;
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
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
class ChannelServiceImplTest {

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private VideoService videoService;

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ChannelServiceImpl channelService;

    private MockedStatic<YoutubeUtil> youtubeUtilMock;

    @BeforeEach
    void setUp() {
        youtubeUtilMock = mockStatic(YoutubeUtil.class);
        // Shorts 판별 시 RestTemplate HEAD 요청 mock — 기본적으로 Shorts 아님 처리
        lenient().when(restTemplate.exchange(anyString(), eq(HttpMethod.HEAD), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
    }

    @AfterEach
    void tearDown() {
        youtubeUtilMock.close();
    }

    @Nested
    @DisplayName("updateOrCreateChannelByMember는")
    class Describe_updateOrCreateChannelByMember {

        @Nested
        @DisplayName("신규 사용자이면")
        class Context_with_new_member {

            @Test
            @DisplayName("새 채널을 생성하고 영상을 저장한다")
            void it_creates_new_channel_and_saves_videos() {
                // given
                Member member = createMember(1L);
                YoutubeChannelResDTO.Item channelItem = createChannelItem();
                List<YoutubeVideoBriefDTO> briefs = createBriefs();
                List<YoutubeVideoDetailDTO> details = createDetails();
                Channel savedChannel = createChannel(1L, member);

                given(redisUtil.getGoogleAccessToken(1L)).willReturn("access-token");
                given(channelRepository.findByMember(member)).willReturn(Optional.empty());
                given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);

                youtubeUtilMock.when(() -> YoutubeUtil.getChannelDetails("access-token"))
                        .thenReturn(channelItem);
                youtubeUtilMock.when(() -> YoutubeUtil.getAllVideoShares(eq("access-token"), any(), any()))
                        .thenReturn(100L);
                youtubeUtilMock.when(() -> YoutubeUtil.getVideosBriefsByPlayListId("access-token", "UU123"))
                        .thenReturn(briefs);
                youtubeUtilMock.when(() -> YoutubeUtil.getVideoDetailsByIds(eq("access-token"), anyList()))
                        .thenReturn(details);

                // when
                Channel result = channelService.updateOrCreateChannelByMember(member);

                // then
                assertThat(result).isNotNull();
                verify(channelRepository, times(2)).save(any(Channel.class));
                verify(videoService, times(2)).updateVideo(any(), any(), any());
            }
        }

        @Nested
        @DisplayName("기존 사용자이면")
        class Context_with_existing_member {

            @Test
            @DisplayName("기존 채널을 업데이트하고 영상을 갱신한다")
            void it_updates_existing_channel() {
                // given
                Member member = createMember(1L);
                Channel existingChannel = createChannel(1L, member);
                YoutubeChannelResDTO.Item channelItem = createChannelItem();
                List<YoutubeVideoBriefDTO> briefs = createBriefs();
                List<YoutubeVideoDetailDTO> details = createDetails();

                given(redisUtil.getGoogleAccessToken(1L)).willReturn("access-token");
                given(channelRepository.findByMember(member)).willReturn(Optional.of(existingChannel));
                given(channelRepository.save(any(Channel.class))).willReturn(existingChannel);

                youtubeUtilMock.when(() -> YoutubeUtil.getChannelDetails("access-token"))
                        .thenReturn(channelItem);
                youtubeUtilMock.when(() -> YoutubeUtil.getAllVideoShares(eq("access-token"), any(), any()))
                        .thenReturn(100L);
                youtubeUtilMock.when(() -> YoutubeUtil.getVideosBriefsByPlayListId("access-token", "UU123"))
                        .thenReturn(briefs);
                youtubeUtilMock.when(() -> YoutubeUtil.getVideoDetailsByIds(eq("access-token"), anyList()))
                        .thenReturn(details);

                // when
                Channel result = channelService.updateOrCreateChannelByMember(member);

                // then
                assertThat(result).isEqualTo(existingChannel);
                // 기존 채널이 있으면 save는 마지막 1회만 (새 채널 생성 save 없음)
                verify(channelRepository, times(1)).save(any(Channel.class));
                verify(videoService, times(2)).updateVideo(any(), any(), any());
            }
        }

        @Nested
        @DisplayName("영상의 통계를 계산할 때")
        class Context_when_calculating_stats {

            @Test
            @DisplayName("모든 영상의 좋아요와 댓글을 합산한다")
            void it_accumulates_likes_and_comments() {
                // given
                Member member = createMember(1L);
                Channel existingChannel = createChannel(1L, member);
                YoutubeChannelResDTO.Item channelItem = createChannelItem();

                YoutubeVideoBriefDTO brief1 = new YoutubeVideoBriefDTO("vid1", "http://thumb1", "제목1", "2024-01-01T00:00:00Z");
                YoutubeVideoBriefDTO brief2 = new YoutubeVideoBriefDTO("vid2", "http://thumb2", "제목2", "2024-01-02T00:00:00Z");
                YoutubeVideoDetailDTO detail1 = new YoutubeVideoDetailDTO("설명1", "24", 1000L, 50L, 10L);
                YoutubeVideoDetailDTO detail2 = new YoutubeVideoDetailDTO("설명2", "24", 2000L, 30L, 20L);

                given(redisUtil.getGoogleAccessToken(1L)).willReturn("access-token");
                given(channelRepository.findByMember(member)).willReturn(Optional.of(existingChannel));
                given(channelRepository.save(any(Channel.class))).willReturn(existingChannel);

                youtubeUtilMock.when(() -> YoutubeUtil.getChannelDetails("access-token"))
                        .thenReturn(channelItem);
                youtubeUtilMock.when(() -> YoutubeUtil.getAllVideoShares(eq("access-token"), any(), any()))
                        .thenReturn(100L);
                youtubeUtilMock.when(() -> YoutubeUtil.getVideosBriefsByPlayListId("access-token", "UU123"))
                        .thenReturn(List.of(brief1, brief2));
                youtubeUtilMock.when(() -> YoutubeUtil.getVideoDetailsByIds(eq("access-token"), anyList()))
                        .thenReturn(List.of(detail1, detail2));

                // when
                Channel result = channelService.updateOrCreateChannelByMember(member);

                // then
                // updateChannelStats 호출 후 likeCount = 50+30 = 80, comment = 10+20 = 30
                assertThat(result.getLikeCount()).isEqualTo(80L);
                assertThat(result.getComment()).isEqualTo(30L);
            }
        }
    }

    // --- Fixture ---

    private Member createMember(Long id) {
        return Member.builder()
                .id(id)
                .nickname("테스트")
                .googleEmail("test@test.com")
                .googleId("google-123")
                .plan(SubscriptionPlan.FREE)
                .status(MemberStatus.ACTIVE)
                .build();
    }

    private Channel createChannel(Long id, Member member) {
        return Channel.builder()
                .id(id)
                .member(member)
                .youtubeChannelId("UC123")
                .youtubePlaylistId("UU123")
                .name("테스트 채널")
                .view(1000L)
                .likeCount(0L)
                .subscribe(500L)
                .share(50L)
                .videoCount(10L)
                .comment(0L)
                .link("https://youtube.com/channel/UC123")
                .joinDate(LocalDateTime.of(2024, 1, 1, 0, 0))
                .target("default")
                .concept("default")
                .image("http://img.url")
                .channelUpdateAt(LocalDateTime.now())
                .build();
    }

    private YoutubeChannelResDTO.Item createChannelItem() {
        // YoutubeChannelResDTO는 Jackson DTO라 직접 생성이 어려움
        // Reflection이나 ObjectMapper를 사용해서 생성
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            String json = """
                    {
                        "id": "UC123",
                        "snippet": {
                            "title": "테스트 채널",
                            "customUrl": "@test",
                            "publishedAt": "2024-01-01T00:00:00",
                            "thumbnails": {
                                "default": {
                                    "url": "http://img.url",
                                    "width": 88,
                                    "height": 88
                                }
                            }
                        },
                        "statistics": {
                            "viewCount": 1000,
                            "subscriberCount": 500,
                            "videoCount": 10
                        },
                        "contentDetails": {
                            "relatedPlaylists": {
                                "uploads": "UU123"
                            }
                        }
                    }
                    """;
            return mapper.readValue(json, YoutubeChannelResDTO.Item.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
