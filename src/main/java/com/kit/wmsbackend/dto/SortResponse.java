package com.kit.wmsbackend.dto;

import jakarta.validation.constraints.NotBlank;

public record SortResponse(
        @NotBlank(message = "Sort field cannot be empty")
        String field,

        @NotBlank(message = "Sort direction cannot be empty")
        String direction
) {
}
