package com.kit.wmsbackend.dto;

import jakarta.validation.constraints.Size;

public record PaginationRequest(
        @Size(min = 1, message = "Page number must be at least 1")
        Integer page,

        @Size(min = 10, message = "Page size must be at least 10")
        Integer size
) {
}
