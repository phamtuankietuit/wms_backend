package com.kit.wmsbackend.constant;

public class SecurityConstant {
    public static final String[] PUBLIC_ENDPOINTS = {
        "/login",
        "/register",
        "/refresh-token",
        "/swagger-ui/**",
        "/v3/api-docs/**"
    };
}
