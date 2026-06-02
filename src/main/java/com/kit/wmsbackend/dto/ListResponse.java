package com.kit.wmsbackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ListResponse<T>(
        @NotNull(message = "Data must not be null")
        T data,

        @Valid PaginationResponse pagination
) {
}
