package com.kit.wmsbackend.feature.stocktransaction.dto;

import java.util.UUID;

public record AssignedToResponse(
        UUID id,
        String name
) {
}
