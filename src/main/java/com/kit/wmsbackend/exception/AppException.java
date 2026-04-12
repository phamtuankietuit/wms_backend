package com.kit.wmsbackend.exception;

import com.kit.wmsbackend.enums.ErrorCode;
import lombok.Getter;
import org.jspecify.annotations.NonNull;

@Getter
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;

    public AppException(@NonNull ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AppException(@NonNull ErrorCode errorCode, String additionalMessage) {
        super(errorCode.getMessage() + " " + additionalMessage);
        this.errorCode = errorCode;
    }
}
