package com.kit.wmsbackend.enums;

import com.kit.wmsbackend.exception.AppException;

public enum UserStatus {
    PENDING,
    ACTIVE,
    DISABLED;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean canTransitionTo(UserStatus nextStatus) {
        if (nextStatus == null) {
            return false;
        }

        return switch (this) {
            case PENDING -> nextStatus == ACTIVE || nextStatus == DISABLED;
            case ACTIVE -> nextStatus == DISABLED;
            case DISABLED -> nextStatus == ACTIVE;
        };
    }

    public void validateTransitionTo(UserStatus nextStatus) {
        if (canTransitionTo(nextStatus)) {
            return;
        }

        throw new AppException(
                ErrorCode.USER_INVALID_STATUS_TRANSITION,
                this + " -> " + nextStatus
        );
    }
}
