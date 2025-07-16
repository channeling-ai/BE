package channeling.be.global.infrastructure.batch;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.channel.domain.repository.ChannelRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class BatchConfig {
    private final EntityManagerFactory entityManagerFactory;
    private final ChannelRepository channelRepository;

    private static final int CHUNK_SIZE = 10;

    // 현재 버전에선 자동으로 해주기 때문에 필요 없음
    // =================================================================
    // == Redis를 사용하기 위한 핵심 배치 컴포넌트 재정의 ==
    // =================================================================

    // JobRepository를 직접 정의합니다. 이렇게 하면 Spring Boot는 JDBC 기반의 JobRepository를 생성하지 않습니다.
    // 참고: Spring Batch는 공식적으로 Redis JobRepository를 지원하지 않아,
    // 이 방식은 트랜잭션 없이 동작하는 'Resourceless' 방식을 사용합니다.
    // 즉, 메타데이터의 트랜잭션 무결성이 완벽하게 보장되지는 않지만,
    // 대부분의 시나리오에서는 충분히 동작합니다.
//    @Bean
//    public JobRepository jobRepository() {
//        // 인메모리 기반의 메타데이터 관리를 위한 JobRepository 생성
//        return new SimpleJobRepository(new org.springframework.batch.core.repository.dao.MapJobInstanceDao(),
//                new org.springframework.batch.core.repository.dao.MapJobExecutionDao(),
//                new org.springframework.batch.core.repository.dao.MapStepExecutionDao(),
//                new org.springframework.batch.core.repository.dao.MapExecutionContextDao());
//    }
//
//    @Bean
//    public JobExplorer jobExplorer() {
//        // 인메모리 기반의 JobExplorer 생성
//        return new SimpleJobExplorer(new org.springframework.batch.core.repository.dao.MapJobInstanceDao(),
//                new org.springframework.batch.core.repository.dao.MapJobExecutionDao(),
//                new org.springframework.batch.core.repository.dao.MapStepExecutionDao(),
//                new org.springframework.batch.core.repository.dao.MapExecutionContextDao());
//    }
//
//    @Bean
//    public JobLauncher jobLauncher(JobRepository jobRepository) {
//        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
//        jobLauncher.setJobRepository(jobRepository);
//        return jobLauncher;
//    }

    // =================================================================
    // == 실제 배치 잡(Job)과 스텝(Step) 정의 ==
    // =================================================================

    @Bean
    public Job updateChannelJob(JobRepository jobRepository, Step youtubeChannelUpdateStep) {
        return new JobBuilder("updateChannelJob", jobRepository)
                .start(youtubeChannelUpdateStep)
                .build();
    }

    @Bean
    public Step updateChannelStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("updateChannelStep", jobRepository)
                .<Channel, Channel>chunk(CHUNK_SIZE, transactionManager) // <Input, Output> 타입
                .reader(channelItemReader())       // Reader: DB에서 Channel 데이터 읽기
                .processor(channelItemProcessor()) // Processor: 외부 API로 데이터 가공
                .writer(channelItemWriter())       // Writer: 변경된 데이터 DB에 저장
                .faultTolerant()                   // 실패 처리 기능 활성화
                .skip(RuntimeException.class)      // 예외 발생 시 Skip
                // .skipLimit(10) // 최대 10번까지 Skip 허용
                .build();
    }

    @Bean
    public JpaPagingItemReader<Channel> channelItemReader() {
        return new JpaPagingItemReaderBuilder<Channel>()
                .name("channelItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("SELECT c FROM Channel c ORDER BY c.id ASC")
                .build();
    }

    @Bean
    public ItemProcessor<Channel, Channel> channelItemProcessor() {
        return channel -> {
            log.info("Processing channel ID: {}", channel.getId());
            try {

                // TODO 외부 API 호출 및 Channel 데이터 가공 로직

                // 변경된 Channel 객체를 Writer로 전달
                return channel;

            } catch (Exception e) {
                log.error("Error processing channel ID {}: {}", channel.getId(), e.getMessage());
                throw new RuntimeException("Failed to process channel " + channel.getId(), e);
            }
        };
    }

    @Bean
    public ItemWriter<Channel> channelItemWriter() { // JpaItemWriter를 사용하지 않고 직접 구현
        return items -> {
            log.info("Writing {} channels.", items.size());
            channelRepository.saveAll(items); // Chunk 단위 저장
        };
    }
}
