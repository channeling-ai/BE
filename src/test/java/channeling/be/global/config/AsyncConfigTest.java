package channeling.be.global.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.assertj.core.api.Assertions.assertThat;

class AsyncConfigTest {

    private final AsyncConfig asyncConfig = new AsyncConfig();

    @Nested
    @DisplayName("getAsyncExecutor는")
    class Describe_getAsyncExecutor {

        @Nested
        @DisplayName("호출되면")
        class Context_when_called {

            @Test
            @DisplayName("올바른 스레드풀 설정을 반환한다")
            void it_returns_configured_executor() {
                // when
                Executor executor = asyncConfig.getAsyncExecutor();

                // then
                assertThat(executor).isInstanceOf(ThreadPoolTaskExecutor.class);
                ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;
                assertThat(taskExecutor.getCorePoolSize()).isEqualTo(5);
                assertThat(taskExecutor.getMaxPoolSize()).isEqualTo(8);
                assertThat(taskExecutor.getThreadNamePrefix()).isEqualTo("async-");
            }

            @Test
            @DisplayName("CallerRunsPolicy를 사용한다")
            void it_uses_caller_runs_policy() {
                // when
                ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) asyncConfig.getAsyncExecutor();
                ThreadPoolExecutor threadPoolExecutor = executor.getThreadPoolExecutor();

                // then
                assertThat(threadPoolExecutor.getRejectedExecutionHandler())
                        .isInstanceOf(ThreadPoolExecutor.CallerRunsPolicy.class);
            }
        }
    }
}
