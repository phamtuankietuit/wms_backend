package com.kit.wmsbackend.feature.user.dto;

import java.util.UUID;

public record RoleUResponse(
        UUID id,
        String name,
        String code,
        Boolean isAdminRole,
        Boolean isSystemRole
) {
}
