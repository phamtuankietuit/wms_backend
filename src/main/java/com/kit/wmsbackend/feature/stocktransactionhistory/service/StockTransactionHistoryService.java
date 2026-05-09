package com.kit.wmsbackend.feature.stocktransactionhistory.service;

import com.kit.wmsbackend.feature.stocktransactionhistory.dto.StockTransactionHistoryRequest;
import jakarta.validation.Valid;

public interface StockTransactionHistoryService {
    void logHistory(@Valid StockTransactionHistoryRequest request);
}
