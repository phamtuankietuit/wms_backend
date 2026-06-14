package com.kit.wmsbackend.exception;

import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.enums.ErrorCode;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(@NonNull MethodArgumentNotValidException exception) {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            String field = fieldError.getField();
            String message = fieldError.getDefaultMessage();
            if (message == null) {
                continue;
            }
            List<String> fieldMessages = errors.computeIfAbsent(field, ignored -> new ArrayList<>());
            if (!fieldMessages.contains(message)) {
                fieldMessages.add(message);
            }
        }

        ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode, errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(@NonNull ConstraintViolationException exception) {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            String property = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            List<String> propertyMessages = errors.computeIfAbsent(property, ignored -> new ArrayList<>());
            if (!propertyMessages.contains(message)) {
                propertyMessages.add(message);
            }
        }

        ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode, errors));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(@NonNull MethodArgumentTypeMismatchException exception) {
        String message = "Invalid value for parameter: " + exception.getName();
        ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode, message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMessageNotReadable(@NonNull HttpMessageNotReadableException exception) {
        ErrorCode errorCode = ErrorCode.HTTP_MESSAGE_NOT_READABLE;
        log.warn("Failed to read HTTP message: {}", exception.getMostSpecificCause().getMessage());
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(@NonNull EntityNotFoundException exception) {
        ErrorCode errorCode = ErrorCode.RESOURCE_NOT_FOUND;
        log.warn("Entity not found: {}", exception.getMessage());
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(@NonNull DataIntegrityViolationException exception) {
        ErrorCode errorCode = ErrorCode.DATA_INTEGRITY_VIOLATION;
        log.warn("Data integrity violation: {}", exception.getMostSpecificCause().getMessage());
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials() {
        ErrorCode errorCode = ErrorCode.AUTH_INVALID_CREDENTIALS;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode));
    }

    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccountStatus() {
        ErrorCode errorCode = ErrorCode.AUTH_INVALID_ACCOUNT;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthentication() {
        ErrorCode errorCode = ErrorCode.AUTH_UNAUTHORIZED;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleJwtException() {
        ErrorCode errorCode = ErrorCode.JWT_INVALID_OR_EXPIRED_TOKEN;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameter(
            @NonNull MissingServletRequestParameterException exception
    ) {
        String parameterName = exception.getParameterName();
        String parameterType = exception.getParameterType();
        String message = String.format("Missing required parameter: '%s' (expected type: %s)", parameterName, parameterType);
        Map<String, List<String>> errors = new LinkedHashMap<>();
        errors.put(parameterName, List.of("Missing required parameter. Expected type: " + parameterType));

        log.warn("{}", message);
        ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode, errors));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingRequestHeader(
            @NonNull MissingRequestHeaderException exception
    ) {
        String headerName = exception.getHeaderName();
        String message = String.format("Missing required header: '%s'", headerName);
        Map<String, List<String>> errors = new LinkedHashMap<>();
        errors.put(headerName, List.of("Missing required request header."));

        log.warn("{}", message);
        ErrorCode errorCode = ErrorCode.REQUEST_HEADER_MISSING;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode, errors));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceeded() {
        ErrorCode errorCode = ErrorCode.CLOUDINARY_FILE_TOO_LARGE;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode));
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(@NonNull AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        int status = errorCode.getStatus();
        String errorCodeName = errorCode.name();

        if (status >= 500) {
            log.error("[{}] Application error: {}", errorCodeName, exception.getMessage(), exception);
        } else if (status == 401 || status == 403) {
            log.warn("[{}] Authentication/Authorization error: {}", errorCodeName, exception.getMessage());
        } else {
            log.debug("[{}] Application error: {}", errorCodeName, exception.getMessage());
        }
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(errorCode, exception.getMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatus(@NonNull ResponseStatusException exception) {
        ErrorCode errorCode = resolveResponseStatusErrorCode(exception);
        String message = exception.getStatusCode().is4xxClientError() ? exception.getReason() : null;
        if (!exception.getStatusCode().is4xxClientError()) {
            log.error("Unhandled response status exception", exception);
        }
        return ResponseEntity
                .status(exception.getStatusCode())
                .body(ApiResponse.error(errorCode, message));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthorizationDenied() {
        ErrorCode errorCode = ErrorCode.AUTH_FORBIDDEN;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFound() {
        ErrorCode errorCode = ErrorCode.SERVER_RESOURCE_NOT_FOUND;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(@NonNull IllegalArgumentException exception) {
        ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode, exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception exception) {
        log.error("Unhandled exception", exception);
        ErrorCode errorCode = ErrorCode.SERVER_INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode));
    }

    private ErrorCode resolveResponseStatusErrorCode(@NonNull ResponseStatusException exception) {
        return switch (exception.getStatusCode().value()) {
            case 401 -> ErrorCode.AUTH_UNAUTHORIZED;
            case 403 -> ErrorCode.AUTH_FORBIDDEN;
            case 404 -> ErrorCode.SERVER_RESOURCE_NOT_FOUND;
            default -> exception.getStatusCode().is4xxClientError()
                    ? ErrorCode.VALIDATION_FAILED
                    : ErrorCode.SERVER_INTERNAL_SERVER_ERROR;
        };
    }
}

