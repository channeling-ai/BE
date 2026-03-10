package channeling.be.global.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 기본 스레드 수 — 평상시 유지되는 스레드 개수
        executor.setCorePoolSize(5);

        // 최대 스레드 수 — 큐가 가득 찼을 때 최대로 늘어나는 스레드 개수
        // HikariCP maximumPoolSize(20)보다 작게 설정하여 동기 요청용 커넥션 여유 확보
        executor.setMaxPoolSize(8);

        // 대기 큐 용량 — core 스레드가 모두 바쁠 때 작업이 대기하는 큐 크기
        executor.setQueueCapacity(50);

        // 스레드 이름 접두사 — 로그에서 비동기 스레드를 쉽게 식별하기 위함
        executor.setThreadNamePrefix("async-");

        // 거부 정책 — 큐도 가득 차면 호출한 스레드에서 직접 실행 (작업 유실 방지)
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) ->
                log.error("비동기 작업 예외 - method: {}", method.getName(), ex);
    }
}
