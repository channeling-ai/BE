package channeling.be.global.infrastructure.batch;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.channel.domain.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChannelScheduler {

    private final ChannelRepository channelRepository;

    /**
     * 매일 새벽 1시에 채널 정보 업데이트 배치 실행
     * cron = "초 분 시 일 월 요일"
     */
//    @Scheduled(cron = "0 1 0 * * *")
    @Scheduled(fixedDelay = 60000) // TODO 테스트용으로 1분마다 실행
    public void runUpdateChannelJob() {

        log.info("채널 정보 동기화 스케줄러 시작");
        List<Channel> allChannels = channelRepository.findAll();

        for (Channel channel : allChannels) {
            try {
                // TODO 채널 정보 조회 youtube api 호출 및 관련 컬럼 업데이트
            } catch (Exception e) {
                // 예외 발생 시 중단하지 않고 로그 출력 후 다음 채널 실행
                log.error("채널 ID {} 정보 업데이트 실패: {}", channel.getId(), e.getMessage());
            }
        }
        log.info("채널 정보 동기화 스케줄러 종료");
    }
}
