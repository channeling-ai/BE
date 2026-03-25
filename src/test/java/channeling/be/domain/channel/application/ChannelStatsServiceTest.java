package channeling.be.domain.channel.application;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.channel.domain.repository.ChannelRepository;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.member.domain.MemberStatus;
import channeling.be.domain.member.domain.SubscriptionPlan;
import channeling.be.global.infrastructure.youtube.YouTubeApiService;
import channeling.be.global.infrastructure.youtube.dto.res.YoutubeChannelResDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChannelStatsServiceTest {

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private YouTubeApiService youTubeApiService;

    @Mock
    private TransactionTemplate transactionTemplate;

    @InjectMocks
    private ChannelStatsService channelStatsService;

    @BeforeEach
    void setUp() {
        doAnswer(invocation -> {
            Consumer<org.springframework.transaction.TransactionStatus> action = invocation.getArgument(0);
            action.accept(null);
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());
    }

    @Test
    @DisplayName("updateChannelStats는 YouTube API로 통계를 조회하고 DB에 저장한다")
    void it_fetches_stats_and_saves() {
        // given
        Channel channel = createChannel(1L);
        YoutubeChannelResDTO.Item channelItem = createChannelItem();

        given(youTubeApiService.fetchChannelDetails("token")).willReturn(channelItem);
        given(youTubeApiService.fetchAllVideoShares(any(), any(), any())).willReturn(200L);
        given(channelRepository.save(any(Channel.class))).willReturn(channel);

        // when
        channelStatsService.updateChannelStats(channel, "token");

        // then
        verify(youTubeApiService).fetchChannelDetails("token");
        verify(channelRepository).save(channel);
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

    private YoutubeChannelResDTO.Item createChannelItem() {
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
}
