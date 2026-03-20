package com.kit.wmsbackend.enums;

public enum TokenType {
    ACCESS_TOKEN,
    REFRESH_TOKEN,
    RESET_TOKEN;

    @Override
    public String toString() {
        return switch (this) {
            case ACCESS_TOKEN -> "accessToken";
            case REFRESH_TOKEN -> "refreshToken";
            case RESET_TOKEN -> "resetToken";
        };
    }
}
