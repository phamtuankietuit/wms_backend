package com.kit.wmsbackend.constant;

public final class SecurityConstant {
    public static final String[] PUBLIC_ENDPOINTS = {
        "/login",
        "/register",
        "/refresh-token",
        "/forgot-password",
        "/reset-password",
        "/logout",
        "/swagger-ui/**",
        "/v3/api-docs/**"
    };
}
