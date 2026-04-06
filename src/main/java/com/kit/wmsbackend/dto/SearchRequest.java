package com.kit.wmsbackend.dto;

import jakarta.validation.constraints.NotNull;

public record SearchRequest(
        @NotNull(message = "Keyword must not be null")
        String keyword
) {
}
