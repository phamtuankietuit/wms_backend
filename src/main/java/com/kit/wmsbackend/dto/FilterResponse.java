package com.kit.wmsbackend.dto;

public record FilterResponse(
        String field,
        String operator,
        Object value
) {
}
