package com.kit.wmsbackend.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kit.wmsbackend.enums.ErrorCode;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        String code,
        boolean success,
        String message,
        T data,
        Map<String, List<String>> errors,
        Instant timestamp
) {
    private static final String DEFAULT_SUCCESS_MESSAGE = "successful";

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(null, true, DEFAULT_SUCCESS_MESSAGE, data, null, Instant.now());
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(null, true, message, data, null, Instant.now());
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(null, false, message, null, null, Instant.now());
    }

    public static <T> ApiResponse<T> error(String message, Map<String, List<String>> errors) {
        return new ApiResponse<>(null, false, message, null, errors, Instant.now());
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(code, false, message, null, null, Instant.now());
    }

    public static <T> ApiResponse<T> error(String code, String message, Map<String, List<String>> errors) {
        return new ApiResponse<>(code, false, message, null, errors, Instant.now());
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return error(errorCode.name(), errorCode.getMessage());
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String message) {
        return error(errorCode.name(), resolveMessage(errorCode, message));
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, Map<String, List<String>> errors) {
        return error(errorCode.name(), errorCode.getMessage(), errors);
    }

    public static <T> ApiResponse<T> error(
            ErrorCode errorCode,
            String message,
            Map<String, List<String>> errors
    ) {
        return error(errorCode.name(), resolveMessage(errorCode, message), errors);
    }

    private static String resolveMessage(ErrorCode errorCode, String message) {
        return message == null || message.isBlank() ? errorCode.getMessage() : message;
    }
}

