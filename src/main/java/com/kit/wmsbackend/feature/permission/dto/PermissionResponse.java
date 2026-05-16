package com.kit.wmsbackend.feature.permission.dto;

import java.util.UUID;

public record PermissionResponse(
        UUID id,
        String code,
        String name
) {
}
