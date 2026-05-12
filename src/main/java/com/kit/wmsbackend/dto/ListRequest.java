package com.kit.wmsbackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ListRequest (
        @Valid List<FilterRequest> filters,

        @Valid SearchRequest search,

        @NotNull
        @Valid
        PaginationRequest pagination,

        @Valid SortRequest sort
) {
}
