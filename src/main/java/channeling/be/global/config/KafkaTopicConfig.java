package channeling.be.global.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topic.overview}")
    private String overviewTopic;

    @Value("${spring.kafka.topic.analysis}")
    private String analysisTopic;

    @Value("${spring.kafka.topic.partitions}")
    private int partitions;

    @Value("${spring.kafka.topic.replicas}")
    private int replicas;

    @Bean
    public NewTopic overviewTopic() {
        return TopicBuilder.name(overviewTopic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic analysisTopic() {
        return TopicBuilder.name(analysisTopic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
