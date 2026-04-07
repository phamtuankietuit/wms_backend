package com.kit.wmsbackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ListResponse<T>(
        @NotNull(message = "Data must not be null")
        T data,

        @Valid List<FilterResponse> filters,

        @NotNull(message = "Pagination information must not be null")
        PaginationResponse pagination,

        @Valid SortResponse sort
) {
}
