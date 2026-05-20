package com.kit.wmsbackend.feature.stocktransaction.service;

import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.feature.stocktransaction.dto.*;
import com.kit.wmsbackend.feature.stocktransactionhistory.dto.StockTransactionHistoryResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface StockTransactionService {
    StockTransactionResponse create(@Valid StockTransactionRequest stockTransactionRequest);
    StockTransactionResponse updateForDraft(UUID id, @Valid StockTransactionUpdateForDraftRequest request);
    StockTransactionResponse changeStatus(UUID id, @Valid StockTransactionStatusRequest request);
    List<StockTransactionResponse> bulkChangeStatus(@Valid StockTransactionBulkStatusRequest request);
    StockTransactionResponse getById(UUID id);
    ListResponse<List<StockTransactionResponse>> list(@Valid ListRequest listRequest);
    ListResponse<List<StockTransactionItemResponse>> listItemByStockTransactionId(
            UUID stockTransactionId,
            @Valid StockTransactionItemListRequest request
    );
    ListResponse<List<StockTransactionHistoryResponse>> listHistoryByStockTransactionId(UUID stockTransactionId, @Valid ListRequest listRequest);
}
