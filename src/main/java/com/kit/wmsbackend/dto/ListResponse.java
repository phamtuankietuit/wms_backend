package com.kit.wmsbackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ListResponse<T>(
        @NotNull(message = "Data must not be null")
        T data,

        @Valid List<FilterResponse> filters,

        @Valid PaginationResponse pagination,

        @Valid SortResponse sort
) {
}
