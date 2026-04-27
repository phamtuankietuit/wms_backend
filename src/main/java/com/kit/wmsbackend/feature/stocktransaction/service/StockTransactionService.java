package com.kit.wmsbackend.feature.stocktransaction.service;

import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionRequest;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionResponse;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionStatusRequest;
import jakarta.validation.Valid;

import java.util.UUID;

public interface StockTransactionService {
    StockTransactionResponse create(@Valid StockTransactionRequest stockTransactionRequest);
    StockTransactionResponse changeStatus(UUID id, @Valid StockTransactionStatusRequest request);
    StockTransactionResponse getById(UUID id);
}
