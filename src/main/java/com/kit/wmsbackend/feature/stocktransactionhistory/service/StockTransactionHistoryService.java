package com.kit.wmsbackend.feature.stocktransactionhistory.service;

import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.feature.stocktransactionhistory.dto.StockTransactionHistoryRequest;
import com.kit.wmsbackend.feature.stocktransactionhistory.dto.StockTransactionHistoryResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface StockTransactionHistoryService {
    void logHistory(@Valid StockTransactionHistoryRequest request);
    ListResponse<List<StockTransactionHistoryResponse>> list(UUID stockTransactionId, @Valid ListRequest listRequest);
}
