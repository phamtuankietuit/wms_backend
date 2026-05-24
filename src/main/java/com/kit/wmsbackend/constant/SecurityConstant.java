package com.kit.wmsbackend.constant;

public final class SecurityConstant {
    public static final String[] PUBLIC_ENDPOINTS = {
        "/login",
        "/register",
        "/refresh-token",
        "/forgot-password",
        "/reset-password",
        "/logout"
    };

    public static final String[] DOCUMENTATION_ENDPOINTS = {
        "/swagger-ui/**",
        "/v3/api-docs/**"
    };
}
