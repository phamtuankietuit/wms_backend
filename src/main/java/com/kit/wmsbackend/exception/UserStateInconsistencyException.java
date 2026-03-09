package com.kit.wmsbackend.exception;

public class UserStateInconsistencyException extends RuntimeException {
    public UserStateInconsistencyException(String message) {
        super(message);
    }
}
