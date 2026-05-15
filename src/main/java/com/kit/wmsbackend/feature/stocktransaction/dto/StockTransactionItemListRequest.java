package com.kit.wmsbackend.feature.stocktransaction.dto;

import com.kit.wmsbackend.dto.PaginationRequest;
import com.kit.wmsbackend.dto.SearchRequest;
import com.kit.wmsbackend.dto.SortRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record StockTransactionItemListRequest(
        @Valid SearchRequest search,

        @Valid SortRequest sort,

        @NotNull
        @Valid PaginationRequest pagination
) {
}
