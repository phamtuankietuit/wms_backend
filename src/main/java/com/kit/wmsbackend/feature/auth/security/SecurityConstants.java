package com.kit.wmsbackend.feature.auth.security;

public class SecurityConstants {
    public static final String[] PUBLIC_ENDPOINTS = {
        "/login",
        "/register",
        "/swagger-ui/**",
        "/v3/api-docs/**"
    };
}
