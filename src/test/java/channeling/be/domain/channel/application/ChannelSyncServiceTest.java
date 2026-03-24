package channeling.be.domain.channel.application;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.member.domain.MemberStatus;
import channeling.be.domain.member.domain.SubscriptionPlan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChannelSyncServiceTest {

    @Mock
    private ChannelService channelService;

    @InjectMocks
    private ChannelSyncService channelSyncService;

    @Nested
    @DisplayName("syncChannelAsync는")
    class Describe_syncChannelAsync {

        @Nested
        @DisplayName("정상적인 멤버가 주어지면")
        class Context_with_valid_member {

            @Test
            @DisplayName("채널 동기화를 수행한다")
            void it_syncs_channel() {
                // given
                Member member = createMember(1L);
                Channel channel = createChannel(1L, member);
                given(channelService.updateOrCreateChannelByMember(member)).willReturn(channel);

                // when
                channelSyncService.syncChannelAsync(member);

                // then
                verify(channelService).updateOrCreateChannelByMember(member);
            }
        }

        @Nested
        @DisplayName("동기화 중 예외가 발생하면")
        class Context_when_exception_occurs {

            @Test
            @DisplayName("예외를 catch하고 로그만 남긴다 (예외가 전파되지 않는다)")
            void it_catches_exception() {
                // given
                Member member = createMember(1L);
                given(channelService.updateOrCreateChannelByMember(member))
                        .willThrow(new RuntimeException("YouTube API 오류"));

                // when & then
                assertThatCode(() -> channelSyncService.syncChannelAsync(member))
                        .doesNotThrowAnyException();
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
}
