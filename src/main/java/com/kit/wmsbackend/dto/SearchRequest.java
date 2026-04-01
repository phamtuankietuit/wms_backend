package com.kit.wmsbackend.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public record SearchRequest(
        String keyword,
        List<String> searchFields
) {
}
