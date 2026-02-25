package com.kit.wmsbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider") // Liên kết với Bean bên dưới
public class JpaAuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        // Trong dự án thực tế, đoạn này sẽ lấy từ SecurityContextHolder (Spring Security)
        // Hiện tại trả về một chuỗi cố định để test hoặc lấy từ hệ thống
        return () -> Optional.of("SYSTEM_USER");
    }
}