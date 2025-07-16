package channeling.be.global.infrastructure.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChannelScheduler {

    private final JobLauncher jobLauncher;
    private final Job updateChannelJob; // BatchConfig에 정의된 Job Bean

    /**
     * 매일 새벽 1시에 채널 정보 업데이트 배치 실행
     * cron = "초 분 시 일 월 요일"
     */
//    @Scheduled(cron = "0 1 0 * * *")
    @Scheduled(fixedDelay = 60000) // TODO 테스트용으로 1분마다 실행
    public void runUpdateChannelJob() {
        try {
            log.info("채널 정보 업데이트 배치 잡을 실행합니다.");

            // 동일한 잡을 반복 실행하기 위해 새 JobParameters 생성
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("datetime", LocalDateTime.now().toString())
                    .toJobParameters();

            jobLauncher.run(updateChannelJob, jobParameters);

        } catch (Exception e) {
            log.error("채널 업데이트 배치 잡 실행 중 오류가 발생했습니다.", e);
        }
    }
}
