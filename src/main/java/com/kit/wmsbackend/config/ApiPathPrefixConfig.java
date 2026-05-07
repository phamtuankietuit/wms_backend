package com.kit.wmsbackend.config;

import com.kit.wmsbackend.annotation.ApiPrefix;
import com.kit.wmsbackend.config.properties.ApiProperties;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class ApiPathPrefixConfig implements WebMvcConfigurer {
    private final ApiProperties apiProperties;

    @Override
    public void configurePathMatch(@NonNull PathMatchConfigurer configurer) {
        if (apiProperties == null || apiProperties.prefix().isBlank()) {
            return;
        }

        String normalizedPrefix = apiProperties.prefix().startsWith("/") ? apiProperties.prefix() : "/" + apiProperties.prefix();
        configurer.addPathPrefix(normalizedPrefix, HandlerTypePredicate.forAnnotation(ApiPrefix.class));
    }
}