package com.kit.wmsbackend.feature.stocktransaction.service;

import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionItemListRequest;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionItemResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface StockTransactionItemService {
    ListResponse<List<StockTransactionItemResponse>> listByStockTransactionId(
            UUID stockTransactionId,
            @Valid StockTransactionItemListRequest request
    );
}
