package channeling.be.domain.channel.application;

import channeling.be.domain.member.domain.Member;
import channeling.be.domain.member.domain.MemberStatus;
import channeling.be.domain.member.domain.SubscriptionPlan;
import channeling.be.global.config.AsyncConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willAnswer;

/**
 * @Async가 실제로 별도 스레드에서 실행되는지 검증하는 슬라이스 테스트.
 * DB/Redis 없이 AsyncConfig + ChannelSyncService만 로드한다.
 */
@SpringJUnitConfig(ChannelSyncServiceAsyncTest.Config.class)
class ChannelSyncServiceAsyncTest {

    @TestConfiguration
    @EnableAsync
    @Import({AsyncConfig.class, ChannelSyncService.class})
    static class Config {
    }

    @Autowired
    private ChannelSyncService channelSyncService;

    @MockBean
    private ChannelService channelService;

    @Nested
    @DisplayName("syncChannelAsync는")
    class Describe_syncChannelAsync {

        @Test
        @DisplayName("호출 스레드와 다른 스레드(async- 접두사)에서 실행된다")
        void it_runs_on_async_thread() throws InterruptedException {
            // given
            Member member = createMember(1L);
            AtomicReference<String> asyncThreadName = new AtomicReference<>();
            CountDownLatch latch = new CountDownLatch(1);

            willAnswer(invocation -> {
                asyncThreadName.set(Thread.currentThread().getName());
                latch.countDown();
                return null;
            }).given(channelService).updateOrCreateChannelByMember(any(Member.class));

            String callerThreadName = Thread.currentThread().getName();

            // when
            channelSyncService.syncChannelAsync(member);
            boolean completed = latch.await(5, TimeUnit.SECONDS);

            // then
            assertThat(completed).isTrue();
            assertThat(asyncThreadName.get()).isNotEqualTo(callerThreadName);
            assertThat(asyncThreadName.get()).startsWith("async-");
        }
    }

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
}
