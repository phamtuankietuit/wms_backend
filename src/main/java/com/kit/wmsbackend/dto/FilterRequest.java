package com.kit.wmsbackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FilterRequest(
        String field,
        String operator,
        Object value
) {
}
