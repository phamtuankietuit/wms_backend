package com.kit.wmsbackend.security;

import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.enums.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public final class SecurityErrorResponseWriter {
    private final ObjectMapper objectMapper;

    public void write(
            @NonNull HttpServletResponse response,
            @NonNull ErrorCode errorCode
    ) throws IOException {
        response.setStatus(errorCode.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(buildErrorResponse(errorCode.getMessage(), errorCode.name()));
    }

    private @NonNull String buildErrorResponse(String message, String code) {
        ApiResponse<?> errorResponse = ApiResponse.error(code, message);
        return objectMapper.writeValueAsString(errorResponse);
    }
}
