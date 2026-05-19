package com.kit.wmsbackend.feature.stocktransaction.controller;

import com.kit.wmsbackend.annotation.ApiPrefix;
import com.kit.wmsbackend.annotation.RequirePermission;
import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.enums.PermissionCode;
import com.kit.wmsbackend.feature.stocktransaction.dto.*;
import com.kit.wmsbackend.feature.stocktransaction.service.StockTransactionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@ApiPrefix
@RestController
@RequestMapping("/stock-transactions")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StockTransactionController {
    StockTransactionService stockTransactionService;

    @PostMapping
    @RequirePermission(PermissionCode.STOCK_TRANSACTION_CREATE)
    public ResponseEntity<ApiResponse<StockTransactionResponse>> create(
            @Valid @RequestBody StockTransactionRequest stockTransactionRequest
    ) {
        return ResponseEntity.ok(ApiResponse.success(stockTransactionService.create(stockTransactionRequest)));
    }

    @PatchMapping("/{id}/draft")
    @RequirePermission(PermissionCode.STOCK_TRANSACTION_UPDATE)
    public ResponseEntity<ApiResponse<StockTransactionResponse>> updateForDraft(
            @PathVariable UUID id,
            @Valid @RequestBody StockTransactionUpdateForDraftRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(stockTransactionService.updateForDraft(id, request)));
    }

    @PostMapping("/{id}/items")
    @RequirePermission(PermissionCode.STOCK_TRANSACTION_READ)
    public ResponseEntity<ApiResponse<ListResponse<List<StockTransactionItemResponse>>>> list(
            @PathVariable UUID id,
            @Valid @RequestBody StockTransactionItemListRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(stockTransactionService.listItemByStockTransactionId(id, request)));
    }

    @PostMapping("/list")
    @RequirePermission(PermissionCode.STOCK_TRANSACTION_READ)
    public ResponseEntity<ApiResponse<ListResponse<List<StockTransactionResponse>>>> list(
            @Valid @RequestBody ListRequest listRequest
    ) {
        return ResponseEntity.ok(ApiResponse.success(stockTransactionService.list(listRequest)));
    }

    @GetMapping("/{id}")
    @RequirePermission(PermissionCode.STOCK_TRANSACTION_READ)
    public ResponseEntity<ApiResponse<StockTransactionResponse>> getById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(ApiResponse.success(stockTransactionService.getById(id)));
    }

    @PatchMapping("/status")
    @RequirePermission(PermissionCode.STOCK_TRANSACTION_UPDATE)
    public ResponseEntity<ApiResponse<List<StockTransactionResponse>>> bulkChangeStatus(
            @Valid @RequestBody StockTransactionBulkStatusRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(stockTransactionService.bulkChangeStatus(request)));
    }

    @PatchMapping("/{id}/status")
    @RequirePermission(PermissionCode.STOCK_TRANSACTION_UPDATE)
    public ResponseEntity<ApiResponse<StockTransactionResponse>> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StockTransactionStatusRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(stockTransactionService.changeStatus(id, request)));
    }
}
