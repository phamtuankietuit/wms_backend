package com.kit.wmsbackend.feature.stocktransaction.service;

import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionRequest;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionResponse;
import jakarta.validation.Valid;

public interface StockTransactionService {
    StockTransactionResponse create(@Valid StockTransactionRequest stockTransactionRequest);
}
