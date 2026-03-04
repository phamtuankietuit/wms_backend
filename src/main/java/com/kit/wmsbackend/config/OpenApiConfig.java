package com.kit.wmsbackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

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
                                new Server().url("http://localhost:8080").description("Server Test")
                        )
                );
    }
}
