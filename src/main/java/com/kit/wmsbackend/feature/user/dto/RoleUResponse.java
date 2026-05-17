package com.kit.wmsbackend.feature.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record RoleUResponse(
        UUID id,
        String name,
        String code,
        @JsonProperty("isAdminRole") boolean adminRole,
        @JsonProperty("isSystemRole") boolean systemRole
) {
}
