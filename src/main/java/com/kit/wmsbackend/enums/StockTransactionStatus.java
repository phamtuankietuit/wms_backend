package com.kit.wmsbackend.enums;

import com.kit.wmsbackend.exception.AppException;

public enum StockTransactionStatus {
    DRAFT,
    PENDING,
    CONFIRMED,
    PROCESSING,
    COMPLETED,
    CANCELLED;

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }

    public boolean canTransitionTo(StockTransactionStatus nextStatus) {
        if (nextStatus == null || isTerminal()) {
            return false;
        }

        return switch (this) {
            case DRAFT -> nextStatus == PENDING || nextStatus == CANCELLED;
            case PENDING -> nextStatus == CONFIRMED || nextStatus == CANCELLED;
            case CONFIRMED -> nextStatus == PROCESSING || nextStatus == CANCELLED;
            case PROCESSING -> nextStatus == COMPLETED || nextStatus == CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };
    }

    public void validateTransitionTo(StockTransactionStatus nextStatus) {
        if (canTransitionTo(nextStatus)) {
            return;
        }

        throw new AppException(
                ErrorCode.STOCK_TRANSACTION_INVALID_STATUS_TRANSITION,
                this + " -> " + nextStatus
        );
    }
}
