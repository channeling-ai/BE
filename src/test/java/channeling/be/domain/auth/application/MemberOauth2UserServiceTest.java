package channeling.be.domain.auth.application;

import channeling.be.domain.auth.application.MemberOauth2UserService.LoginResult;
import channeling.be.domain.auth.application.MemberOauth2UserService.MemberResult;
import channeling.be.domain.member.application.MemberService;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.member.domain.MemberStatus;
import channeling.be.domain.member.domain.SubscriptionPlan;
import channeling.be.global.infrastructure.redis.RedisUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberOauth2UserServiceTest {

    @Mock
    private MemberService memberService;

    @Mock
    private RedisUtil redisUtil;

    @InjectMocks
    private MemberOauth2UserService memberOauth2UserService;

    @Nested
    @DisplayName("processLogin은")
    class Describe_processLogin {

        @Nested
        @DisplayName("신규 사용자가 로그인하면")
        class Context_with_new_user {

            @Test
            @DisplayName("isNew=true를 반환하고 Google 토큰을 저장한다")
            void it_returns_isNew_true() {
                // given
                Member newMember = createMember(1L, MemberStatus.ACTIVE, null);
                Map<String, Object> attrs = createAttrs();

                given(memberService.findOrCreateMember("google-123", "test@test.com", "테스트", "http://pic.url"))
                        .willReturn(new MemberResult(newMember, true));

                // when
                LoginResult result = memberOauth2UserService.processLogin(attrs, "google-access-token");

                // then
                assertThat(result.isNew()).isTrue();
                assertThat(result.member()).isEqualTo(newMember);
                verify(redisUtil).saveGoogleAccessToken(1L, "google-access-token");
            }
        }

        @Nested
        @DisplayName("기존 사용자가 로그인하면")
        class Context_with_existing_user {

            @Test
            @DisplayName("isNew=false를 반환한다")
            void it_returns_isNew_false() {
                // given
                Member existingMember = createMember(1L, MemberStatus.ACTIVE, null);
                Map<String, Object> attrs = createAttrs();

                given(memberService.findOrCreateMember("google-123", "test@test.com", "테스트", "http://pic.url"))
                        .willReturn(new MemberResult(existingMember, false));

                // when
                LoginResult result = memberOauth2UserService.processLogin(attrs, "google-access-token");

                // then
                assertThat(result.isNew()).isFalse();
                assertThat(result.member()).isEqualTo(existingMember);
            }
        }

        @Nested
        @DisplayName("탈퇴 회원이 30일 이내에 로그인하면")
        class Context_with_withdrawn_member_within_30_days {

            @Test
            @DisplayName("회원을 복구한다")
            void it_restores_member() {
                // given
                Member withdrawnMember = createMember(1L, MemberStatus.WITHDRAWN, LocalDateTime.now().minusDays(10));
                Map<String, Object> attrs = createAttrs();

                given(memberService.findOrCreateMember("google-123", "test@test.com", "테스트", "http://pic.url"))
                        .willReturn(new MemberResult(withdrawnMember, false));

                // when
                memberOauth2UserService.processLogin(attrs, "google-access-token");

                // then
                assertThat(withdrawnMember.getStatus()).isEqualTo(MemberStatus.ACTIVE);
                assertThat(withdrawnMember.getDeletedAt()).isNull();
            }
        }

        @Nested
        @DisplayName("탈퇴 회원의 deletedAt이 null이면")
        class Context_with_withdrawn_member_null_deletedAt {

            @Test
            @DisplayName("NPE 없이 정상 반환하며 복구하지 않는다")
            void it_does_not_throw_npe() {
                // given
                Member withdrawnMember = createMember(1L, MemberStatus.WITHDRAWN, null);
                Map<String, Object> attrs = createAttrs();

                given(memberService.findOrCreateMember("google-123", "test@test.com", "테스트", "http://pic.url"))
                        .willReturn(new MemberResult(withdrawnMember, false));

                // when
                LoginResult result = memberOauth2UserService.processLogin(attrs, "google-access-token");

                // then
                assertThat(result).isNotNull();
                assertThat(withdrawnMember.getStatus()).isEqualTo(MemberStatus.WITHDRAWN);
            }
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

    private Map<String, Object> createAttrs() {
        return Map.of(
                "sub", "google-123",
                "email", "test@test.com",
                "name", "테스트",
                "picture", "http://pic.url"
        );
    }
}
