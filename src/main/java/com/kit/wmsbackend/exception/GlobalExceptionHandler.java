package com.kit.wmsbackend.exception;

import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.constant.MessageConstant;
import com.kit.wmsbackend.enums.ErrorCode;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

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

        return ResponseEntity.badRequest().body(ApiResponse.error(
                "VALIDATION_FAILED",
                "Validation failed",
                errors
        ));
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

        return ResponseEntity.badRequest().body(ApiResponse.error(
                "VALIDATION_FAILED",
                "Validation failed",
                errors
        ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        String message = "Invalid value for parameter: " + exception.getName();
        return ResponseEntity.badRequest().body(ApiResponse.error(message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMessageNotReadable(@NonNull HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(ApiResponse.error("Malformed JSON request"));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(@NonNull ResourceNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(
                        e.getMessage()
                ));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(@NonNull EntityNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(@NonNull BadRequestException exception) {
        return ResponseEntity.badRequest().body(ApiResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceAlreadyExists(ResourceAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(@NonNull DataIntegrityViolationException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(exception.getMostSpecificCause().getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(
                        MessageConstant.Authentication.INVALID_CREDENTIALS
                ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthentication(AuthenticationException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(
                        MessageConstant.Authentication.UNAUTHORIZED
                ));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException exception) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleJwtException(JwtException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(
                        MessageConstant.Authentication.INVALID_OR_EXPIRED_TOKEN
                ));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameter(
            @NonNull MissingServletRequestParameterException exception
    ) {
        String parameterName = exception.getParameterName();
        String parameterType = exception.getParameterType();
        String message = String.format("Missing required parameter: '%s' (expected type: %s)", parameterName, parameterType);
        log.error("{}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(message));
    }

    @ExceptionHandler(TokenHashingException.class)
    public ResponseEntity<ApiResponse<Void>> handleTokenHashing(TokenHashingException exception) {
        log.error("Token hashing failure: {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        MessageConstant.Server.INTERNAL_SERVER_ERROR
                ));
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleVariantException(@NonNull AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode.name(), exception.getMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatus(ResponseStatusException exception) {
        String message = exception.getReason() != null ? exception.getReason() : exception.getMessage();
        return ResponseEntity.status(exception.getStatusCode()).body(ApiResponse.error(message));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthorizationDenied(AuthorizationDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(MessageConstant.Authorization.DENIED));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception exception) {
        log.error("Unhandled exception", exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        MessageConstant.Server.INTERNAL_SERVER_ERROR
                ));
    }
}

