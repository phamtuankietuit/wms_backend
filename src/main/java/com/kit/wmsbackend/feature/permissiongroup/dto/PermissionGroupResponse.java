package com.kit.wmsbackend.feature.permissiongroup.dto;

import com.kit.wmsbackend.feature.permission.dto.PermissionResponse;

import java.util.List;
import java.util.UUID;

public record PermissionGroupResponse(
        UUID id,
        String code,
        String name,
        List<PermissionResponse> permissions
) {
}
