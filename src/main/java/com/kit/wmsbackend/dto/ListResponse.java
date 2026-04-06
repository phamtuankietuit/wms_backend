package com.kit.wmsbackend.dto;

import jakarta.validation.constraints.NotNull;

public record ListResponse<T>(
        @NotNull(message = "Data must not be null")
        T data,

        @NotNull(message = "Pagination information must not be null")
        PaginationResponse pagination,

        SortResponse sort
) {
}
