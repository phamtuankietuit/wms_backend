package com.kit.wmsbackend.feature.stocktransaction.service;

import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionRequest;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionResponse;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionStatusRequest;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionUpdateForDraftRequest;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface StockTransactionService {
    StockTransactionResponse create(@Valid StockTransactionRequest stockTransactionRequest);
    StockTransactionResponse updateForDraft(UUID id, @Valid StockTransactionUpdateForDraftRequest request);
    StockTransactionResponse changeStatus(UUID id, @Valid StockTransactionStatusRequest request);
    StockTransactionResponse getById(UUID id);
    ListResponse<List<StockTransactionResponse>> list(@Valid ListRequest listRequest);
}
