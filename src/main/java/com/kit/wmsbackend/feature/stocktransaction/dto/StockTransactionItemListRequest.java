package com.kit.wmsbackend.feature.stocktransaction.dto;

import com.kit.wmsbackend.dto.SearchRequest;
import com.kit.wmsbackend.dto.SortRequest;
import jakarta.validation.Valid;

public record StockTransactionItemListRequest(
        @Valid SearchRequest search,

        @Valid SortRequest sort
) {
}
