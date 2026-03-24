package channeling.be.domain.auth.application;

import channeling.be.domain.auth.application.MemberOauth2UserService.LoginResult;
import channeling.be.domain.auth.application.MemberOauth2UserService.MemberResult;
import channeling.be.domain.channel.application.ChannelService;
import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.channel.domain.repository.ChannelRepository;
import channeling.be.domain.member.application.MemberService;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.member.domain.MemberStatus;
import channeling.be.domain.member.domain.SubscriptionPlan;
import channeling.be.global.infrastructure.redis.RedisUtil;
import channeling.be.response.exception.handler.YoutubeHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberOauth2UserServiceTest {

    @Mock
    private MemberService memberService;

    @Mock
    private ChannelService channelService;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private RedisUtil redisUtil;

    @InjectMocks
    private MemberOauth2UserService memberOauth2UserService;

    @Nested
    @DisplayName("executeGoogleLoginFast는")
    class Describe_executeGoogleLoginFast {

        @Nested
        @DisplayName("신규 사용자가 로그인하면")
        class Context_with_new_user {

            @Test
            @DisplayName("채널을 생성하고 isNew=true로 반환한다")
            void it_creates_channel_and_returns_isNew_true() {
                // given
                Member newMember = createMember(1L, MemberStatus.ACTIVE, null);
                Channel channel = createChannel(1L, newMember);
                Map<String, Object> attrs = createAttrs();

                given(memberService.findOrCreateMember("google-123", "test@test.com", "테스트", "http://pic.url"))
                        .willReturn(new MemberResult(newMember, true));
                given(channelRepository.findByMember(newMember)).willReturn(Optional.empty());
                given(channelService.updateOrCreateChannelByMember(newMember)).willReturn(channel);

                // when
                LoginResult result = memberOauth2UserService.executeGoogleLoginFast(attrs, "google-access-token");

                // then
                assertThat(result.isNew()).isTrue();
                assertThat(result.member()).isEqualTo(newMember);
                assertThat(result.channel()).isEqualTo(channel);
                verify(channelService).updateOrCreateChannelByMember(newMember);
            }
        }

        @Nested
        @DisplayName("기존 사용자가 로그인하면")
        class Context_with_existing_user {

            @Test
            @DisplayName("기존 채널을 반환하고 isNew=false를 반환한다")
            void it_returns_existing_channel() {
                // given
                Member existingMember = createMember(1L, MemberStatus.ACTIVE, null);
                Channel channel = createChannel(1L, existingMember);
                Map<String, Object> attrs = createAttrs();

                given(memberService.findOrCreateMember("google-123", "test@test.com", "테스트", "http://pic.url"))
                        .willReturn(new MemberResult(existingMember, false));
                given(channelRepository.findByMember(existingMember)).willReturn(Optional.of(channel));

                // when
                LoginResult result = memberOauth2UserService.executeGoogleLoginFast(attrs, "google-access-token");

                // then
                assertThat(result.isNew()).isFalse();
                assertThat(result.channel()).isEqualTo(channel);
                verify(channelService, never()).updateOrCreateChannelByMember(any());
            }
        }

        @Nested
        @DisplayName("탈퇴 회원이 30일 이내에 로그인하면")
        class Context_with_withdrawn_member_within_30_days {

            @Test
            @DisplayName("회원을 복구하고 기존 채널을 반환한다")
            void it_restores_member() {
                // given
                Member withdrawnMember = createMember(1L, MemberStatus.WITHDRAWN, LocalDateTime.now().minusDays(10));
                Channel channel = createChannel(1L, withdrawnMember);
                Map<String, Object> attrs = createAttrs();

                given(memberService.findOrCreateMember("google-123", "test@test.com", "테스트", "http://pic.url"))
                        .willReturn(new MemberResult(withdrawnMember, false));
                given(channelRepository.findByMember(withdrawnMember)).willReturn(Optional.of(channel));

                // when
                LoginResult result = memberOauth2UserService.executeGoogleLoginFast(attrs, "google-access-token");

                // then
                assertThat(withdrawnMember.getStatus()).isEqualTo(MemberStatus.ACTIVE);
                assertThat(withdrawnMember.getDeletedAt()).isNull();
                assertThat(result.channel()).isEqualTo(channel);
            }
        }

        @Nested
        @DisplayName("탈퇴 회원의 deletedAt이 null이면")
        class Context_with_withdrawn_member_null_deletedAt {

            @Test
            @DisplayName("NPE가 발생하지 않아야 한다")
            void it_does_not_throw_npe() {
                // given
                // WITHDRAWN 상태이지만 deletedAt이 null인 비정상 데이터
                Member withdrawnMember = createMember(1L, MemberStatus.WITHDRAWN, null);
                Channel channel = createChannel(1L, withdrawnMember);
                Map<String, Object> attrs = createAttrs();

                given(memberService.findOrCreateMember("google-123", "test@test.com", "테스트", "http://pic.url"))
                        .willReturn(new MemberResult(withdrawnMember, false));
                given(channelRepository.findByMember(withdrawnMember)).willReturn(Optional.of(channel));

                // when
                LoginResult result = memberOauth2UserService.executeGoogleLoginFast(attrs, "google-access-token");

                // then — NPE 없이 정상 반환, 복구되지 않음 (deletedAt이 null이므로 복구 조건 불충족)
                assertThat(result).isNotNull();
                assertThat(withdrawnMember.getStatus()).isEqualTo(MemberStatus.WITHDRAWN);
            }
        }

        @Nested
        @DisplayName("채널 없는 계정이면")
        class Context_with_no_youtube_channel {

            @Test
            @DisplayName("YoutubeHandler 예외가 발생한다")
            void it_throws_youtube_handler() {
                // given
                Member newMember = createMember(1L, MemberStatus.ACTIVE, null);
                Map<String, Object> attrs = createAttrs();

                given(memberService.findOrCreateMember("google-123", "test@test.com", "테스트", "http://pic.url"))
                        .willReturn(new MemberResult(newMember, true));
                given(channelRepository.findByMember(newMember)).willReturn(Optional.empty());
                given(channelService.updateOrCreateChannelByMember(newMember))
                        .willThrow(new YoutubeHandler(channeling.be.response.code.status.ErrorStatus._YOUTUBE_CHANNEL_NOT_FOUND));

                // when & then
                assertThatThrownBy(() ->
                        memberOauth2UserService.executeGoogleLoginFast(attrs, "google-access-token"))
                        .isInstanceOf(YoutubeHandler.class);
            }
        }
    }

    @Nested
    @DisplayName("executeGoogleLogin(slow 버전)은")
    class Describe_executeGoogleLogin {

        @Test
        @DisplayName("멤버를 찾거나 생성하고, 채널을 동기로 생성/업데이트한다")
        void it_creates_member_and_syncs_channel() {
            // given
            Member member = createMember(1L, MemberStatus.ACTIVE, null);
            Channel channel = createChannel(1L, member);
            Map<String, Object> attrs = createAttrs();

            given(memberService.findOrCreateMember("google-123", "test@test.com", "테스트", "http://pic.url"))
                    .willReturn(new MemberResult(member, true));
            given(channelService.updateOrCreateChannelByMember(member)).willReturn(channel);

            // when
            LoginResult result = memberOauth2UserService.executeGoogleLogin(attrs, "google-access-token");

            // then
            assertThat(result.member()).isEqualTo(member);
            assertThat(result.channel()).isEqualTo(channel);
            assertThat(result.isNew()).isTrue();
            verify(redisUtil).saveGoogleAccessToken(1L, "google-access-token");
            verify(channelService).updateOrCreateChannelByMember(member);
        }

        @Test
        @DisplayName("기존 사용자도 항상 채널 동기화를 수행한다 (fast 버전과의 차이)")
        void it_always_syncs_channel_even_for_existing_user() {
            // given
            Member member = createMember(1L, MemberStatus.ACTIVE, null);
            Channel channel = createChannel(1L, member);
            Map<String, Object> attrs = createAttrs();

            given(memberService.findOrCreateMember("google-123", "test@test.com", "테스트", "http://pic.url"))
                    .willReturn(new MemberResult(member, false));
            given(channelService.updateOrCreateChannelByMember(member)).willReturn(channel);

            // when
            LoginResult result = memberOauth2UserService.executeGoogleLogin(attrs, "google-access-token");

            // then
            assertThat(result.isNew()).isFalse();
            // slow 버전은 기존 사용자도 동기로 채널 동기화를 수행함
            verify(channelService).updateOrCreateChannelByMember(member);
        }
    }

    // --- Fixture ---

    private Member createMember(Long id, MemberStatus status, LocalDateTime deletedAt) {
        return Member.builder()
                .id(id)
                .nickname("테스트")
                .googleEmail("test@test.com")
                .googleId("google-123")
                .profileImage("http://pic.url")
                .plan(SubscriptionPlan.FREE)
                .status(status)
                .deletedAt(deletedAt)
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
                .likeCount(100L)
                .subscribe(500L)
                .share(50L)
                .videoCount(10L)
                .comment(30L)
                .link("https://youtube.com/channel/UC123")
                .joinDate(LocalDateTime.of(2024, 1, 1, 0, 0))
                .target("default")
                .concept("default")
                .image("http://img.url")
                .channelUpdateAt(LocalDateTime.now())
                .build();
    }

    private Map<String, Object> createAttrs() {
        return Map.of(
                "sub", "google-123",
                "email", "test@test.com",
                "name", "테스트",
                "picture", "http://pic.url"
        );
    }
}
