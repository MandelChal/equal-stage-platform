package com.equal_stage_platform.dev.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "israelTimeAuditorAware")
public class JpaAuditingConfig {

    @Bean
    public DateTimeProvider israelTimeAuditorAware() {
        return new IsraelTimeAuditorAware();
    }
}