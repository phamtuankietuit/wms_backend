package com.kit.wmsbackend.exception;

public class TokenHashingException extends RuntimeException {
    public TokenHashingException(String message, Throwable cause) {
        super(message, cause);
    }
}