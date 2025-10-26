package channeling.be.global.config;

import channeling.be.domain.auth.domain.CustomUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class JpaAuditingConfig {
    @Bean
    public AuditorAware<CustomUserDetails> auditorProvider() {
        return new AuditorAwareConfig();
    }
}
