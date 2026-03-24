package channeling.be.domain.auth.handler;

import channeling.be.domain.auth.application.LoginPostProcessor;
import channeling.be.domain.auth.application.MemberOauth2UserService;
import channeling.be.domain.auth.application.MemberOauth2UserService.LoginResult;
import channeling.be.domain.channel.application.ChannelService;
import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.member.domain.MemberStatus;
import channeling.be.domain.member.domain.SubscriptionPlan;
import channeling.be.global.infrastructure.jwt.JwtUtil;
import channeling.be.global.infrastructure.redis.RedisUtil;
import channeling.be.response.code.status.ErrorStatus;
import channeling.be.response.exception.handler.YoutubeHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class Oauth2LoginSuccessHandlerTest {

    @Mock
    private OAuth2AuthorizedClientService authorizedClientService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private MemberOauth2UserService memberOauth2UserService;

    @Mock
    private ChannelService channelService;

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private LoginPostProcessor loginPostProcessor;

    @InjectMocks
    private Oauth2LoginSuccessHandler handler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(handler, "frontUrl", "http://localhost:5173");
    }

    @Nested
    @DisplayName("onAuthenticationSuccess는")
    class Describe_onAuthenticationSuccess {

        @Nested
        @DisplayName("기존 사용자가 로그인하면")
        class Context_with_existing_user {

            @Test
            @DisplayName("비동기 후처리를 호출하고 리다이렉트한다")
            void it_triggers_async_post_processing() throws Exception {
                // given
                Member member = createMember(1L);
                Channel channel = createChannel(1L, member);
                LoginResult result = new LoginResult(member, false);

                setupOAuthMocks(result, channel);
                given(jwtUtil.createAccessToken(member)).willReturn("jwt-token");

                // when
                handler.onAuthenticationSuccess(request, response, createAuthentication());

                // then
                verify(loginPostProcessor).executeAsync(member, channel, "google-access-token", false);

                ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
                verify(response).sendRedirect(urlCaptor.capture());
                assertThat(urlCaptor.getValue()).contains("isNew=false");
            }
        }

        @Nested
        @DisplayName("신규 사용자가 로그인하면")
        class Context_with_new_user {

            @Test
            @DisplayName("isNew=true로 비동기 후처리를 호출하고 리다이렉트한다")
            void it_calls_async_with_isNew_true() throws Exception {
                // given
                Member member = createMember(1L);
                Channel channel = createChannel(1L, member);
                LoginResult result = new LoginResult(member, true);

                setupOAuthMocks(result, channel);
                given(jwtUtil.createAccessToken(member)).willReturn("jwt-token");

                // when
                handler.onAuthenticationSuccess(request, response, createAuthentication());

                // then
                verify(loginPostProcessor).executeAsync(member, channel, "google-access-token", true);

                ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
                verify(response).sendRedirect(urlCaptor.capture());
                assertThat(urlCaptor.getValue()).contains("isNew=true");
            }
        }

        @Nested
        @DisplayName("YoutubeHandler 예외가 발생하면")
        class Context_when_youtube_handler_thrown {

            @Test
            @DisplayName("에러 리다이렉트 URL로 이동하고 비동기 작업을 호출하지 않는다")
            void it_redirects_to_error_url() throws Exception {
                // given
                Member member = createMember(1L);
                LoginResult result = new LoginResult(member, true);

                setupOAuthMocksBase(result);
                given(channelService.createOrGetBasicChannel(any(), anyString()))
                        .willThrow(new YoutubeHandler(ErrorStatus._YOUTUBE_CHANNEL_NOT_FOUND));

                // when
                handler.onAuthenticationSuccess(request, response, createAuthentication());

                // then
                ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
                verify(response).sendRedirect(urlCaptor.capture());
                assertThat(urlCaptor.getValue()).contains("error=NO_CHANNEL");

                verify(loginPostProcessor, never()).executeAsync(any(), any(), anyString(), anyBoolean());
                verify(jwtUtil, never()).createAccessToken(any());
            }
        }
    }

    // --- Mock Setup ---

    private void setupOAuthMocksBase(LoginResult result) {
        OAuth2AuthorizedClient authorizedClient = mock(OAuth2AuthorizedClient.class);
        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER, "google-access-token", Instant.now(), Instant.now().plusSeconds(3600));
        given(authorizedClient.getAccessToken()).willReturn(accessToken);
        given(authorizedClientService.loadAuthorizedClient(anyString(), anyString())).willReturn(authorizedClient);
        given(memberOauth2UserService.processLogin(anyMap(), anyString())).willReturn(result);
    }

    private void setupOAuthMocks(LoginResult result, Channel channel) {
        setupOAuthMocksBase(result);
        given(channelService.createOrGetBasicChannel(eq(result.member()), anyString())).willReturn(channel);
    }

    private OAuth2AuthenticationToken createAuthentication() {
        Map<String, Object> attrs = Map.of(
                "sub", "google-123",
                "email", "test@test.com",
                "name", "테스트",
                "picture", "http://pic.url"
        );
        OAuth2User oauthUser = new DefaultOAuth2User(
                Collections.emptyList(), attrs, "sub");
        return new OAuth2AuthenticationToken(oauthUser, Collections.emptyList(), "google");
    }

    // --- Fixtures ---

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
