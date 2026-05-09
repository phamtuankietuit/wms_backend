package com.kit.wmsbackend.dto;

import jakarta.validation.Valid;

import java.util.List;

public record ListRequest (
        @Valid List<FilterRequest> filters,

        @Valid SearchRequest search,

        @Valid PaginationRequest pagination,

        @Valid SortRequest sort
) {
}
