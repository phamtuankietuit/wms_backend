package com.kit.wmsbackend.feature.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthGetMeResponse(
        String name,
        String email,
        String avatar,
        Date dateOfBirth
) {
}
