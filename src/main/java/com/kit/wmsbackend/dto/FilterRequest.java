package com.kit.wmsbackend.dto;

public record FilterRequest(
        String field,
        String operator,
        Object value
) {
}
