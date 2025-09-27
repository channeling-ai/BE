package channeling.be.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@Configuration
public class JpaAuditingConfig {
    @Bean
    public AuditorAware<DefaultOAuth2User> auditorProvider() {
        return new AuditorAwareConfig();
    }
}
