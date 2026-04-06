package com.kit.wmsbackend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

public record PaginationRequest(
        @NotNull(message = "Page number is required")
        @Min(value = 1, message = "Page number must be at least 1")
        Integer page,

        @NotNull(message = "Page size is required")
        @Min(value = 10, message = "Page size must be at least 10")
        @Max(value = 100, message = "Page size cannot exceed 100")
        Integer size
) {
}
