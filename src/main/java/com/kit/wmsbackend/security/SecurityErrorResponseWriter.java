package com.kit.wmsbackend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.enums.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@NoArgsConstructor
public final class SecurityErrorResponseWriter {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void write(
            @NonNull HttpServletResponse response,
            @NonNull ErrorCode errorCode
    ) throws IOException {
        response.setStatus(errorCode.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(buildErrorResponse(errorCode.getMessage(), errorCode.name()));
    }

    private static @NonNull String buildErrorResponse(String message, String code) {
        try {
            ApiResponse<?> errorResponse = ApiResponse.error(code, message);
            return objectMapper.writeValueAsString(errorResponse);
        } catch (Exception e) {
            return "{\"code\":\"" + code + "\",\"success\":false,\"message\":\"An error occurred\"}";
        }
    }
}
