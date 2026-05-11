package com.kit.wmsbackend.feature.inventorymovement.dto;

import com.kit.wmsbackend.dto.PaginationRequest;
import com.kit.wmsbackend.dto.SearchRequest;
import com.kit.wmsbackend.dto.SortRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record InventoryMovementListRequest(
        @Valid SearchRequest search,

        @NotNull
        @Valid
        PaginationRequest pagination,

        @Valid SortRequest sort
) {
}
