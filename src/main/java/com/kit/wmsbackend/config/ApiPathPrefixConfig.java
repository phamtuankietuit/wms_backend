package com.kit.wmsbackend.config;

import com.kit.wmsbackend.annotation.ApiPrefix;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiPathPrefixConfig implements WebMvcConfigurer {

    @Value("${app.api.prefix:/api}")
    private String apiPrefix;

    @Override
    public void configurePathMatch(@NonNull PathMatchConfigurer configurer) {
        if (apiPrefix == null || apiPrefix.isBlank()) {
            return;
        }

        String normalizedPrefix = apiPrefix.startsWith("/") ? apiPrefix : "/" + apiPrefix;
        configurer.addPathPrefix(normalizedPrefix, HandlerTypePredicate.forAnnotation(ApiPrefix.class));
    }
}