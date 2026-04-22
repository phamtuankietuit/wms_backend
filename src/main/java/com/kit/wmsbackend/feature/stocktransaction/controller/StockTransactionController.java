package com.kit.wmsbackend.feature.stocktransaction.controller;

import com.kit.wmsbackend.annotation.ApiPrefix;
import com.kit.wmsbackend.annotation.RequirePermission;
import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.enums.PermissionCode;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionRequest;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionResponse;
import com.kit.wmsbackend.feature.stocktransaction.service.StockTransactionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
