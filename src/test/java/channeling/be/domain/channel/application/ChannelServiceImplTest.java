package channeling.be.domain.channel.application;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.channel.domain.repository.ChannelRepository;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.member.domain.MemberStatus;
import channeling.be.domain.member.domain.SubscriptionPlan;
import channeling.be.global.infrastructure.youtube.YouTubeApiService;
import channeling.be.global.infrastructure.youtube.dto.res.YoutubeChannelResDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChannelServiceImplTest {

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private YouTubeApiService youTubeApiService;

    @InjectMocks
    private ChannelServiceImpl channelService;

    @Nested
    @DisplayName("createOrGetBasicChannel은")
    class Describe_createOrGetBasicChannel {

        @Nested
        @DisplayName("신규 사용자이면")
        class Context_with_new_member {

            @Test
            @DisplayName("YouTube API로 채널 정보를 가져와 새 채널을 생성한다")
            void it_creates_new_channel() {
                // given
                Member member = createMember(1L);
                YoutubeChannelResDTO.Item channelItem = createChannelItem();
                Channel savedChannel = createChannel(1L, member);

                given(channelRepository.findByMember(member)).willReturn(Optional.empty());
                given(youTubeApiService.fetchChannelDetails("access-token")).willReturn(channelItem);
                given(youTubeApiService.fetchAllVideoShares(any(), any(), any())).willReturn(100L);
                given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);

                // when
                ChannelService.ChannelCreationResult result = channelService.createOrGetBasicChannel(member, "access-token");

                // then
                assertThat(result.channel()).isNotNull();
                assertThat(result.isChannelNew()).isTrue();
                verify(youTubeApiService).fetchChannelDetails("access-token");
                verify(channelRepository).save(any(Channel.class));
            }
        }

        @Nested
        @DisplayName("기존 사용자이면")
        class Context_with_existing_member {

            @Test
            @DisplayName("기존 채널을 반환하고 YouTube API를 호출하지 않는다")
            void it_returns_existing_channel() {
                // given
                Member member = createMember(1L);
                Channel existingChannel = createChannel(1L, member);

                given(channelRepository.findByMember(member)).willReturn(Optional.of(existingChannel));

                // when
                ChannelService.ChannelCreationResult result = channelService.createOrGetBasicChannel(member, "access-token");

                // then
                assertThat(result.channel()).isEqualTo(existingChannel);
                assertThat(result.isChannelNew()).isFalse();
                verify(channelRepository, never()).save(any(Channel.class));
                verify(youTubeApiService, never()).fetchChannelDetails(any());
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
