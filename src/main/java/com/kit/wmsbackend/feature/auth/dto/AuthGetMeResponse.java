package com.kit.wmsbackend.feature.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthGetMeResponse(
        String name,
        String email,
        String avatarUrl
) {
}
