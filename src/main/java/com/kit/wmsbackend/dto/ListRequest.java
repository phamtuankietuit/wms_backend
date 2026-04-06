package com.kit.wmsbackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ListRequest (
        @Valid SearchRequest search,

        @NotNull(message = "Pagination information is required")
        @Valid PaginationRequest pagination,

        @Valid SortRequest sort
) {
}
