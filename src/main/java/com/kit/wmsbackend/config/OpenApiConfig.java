package com.kit.wmsbackend.config;

import com.kit.wmsbackend.config.properties.ServerProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {
    private final ServerProperties serverProperties;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("WMS Backend API Document")
                                .description("WMS backend API")
                                .version("v1.0.0")
                ).servers(
                        List.of(
                                new Server().url(serverProperties.url()).description("Server")
                        )
                );
    }
}
