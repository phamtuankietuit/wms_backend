package com.kit.wmsbackend.feature.stocktransaction.service;

import com.kit.wmsbackend.constant.AuditConstant;
import com.kit.wmsbackend.entity.*;
import com.kit.wmsbackend.enums.AdjustmentType;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.enums.StockTransactionStatus;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionRequest;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionResponse;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionResult;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionStatusRequest;
import com.kit.wmsbackend.feature.inventory.repository.InventoryRepository;
import com.kit.wmsbackend.feature.stocktransaction.repository.StockTransactionRepository;
import com.kit.wmsbackend.feature.stocktransactionhistory.repository.StockTransactionHistoryRepository;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.mapper.StockTransactionItemMapper;
import com.kit.wmsbackend.mapper.StockTransactionMapper;
import com.kit.wmsbackend.validator.StockTransactionCreateValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StockTransactionServiceImpl implements StockTransactionService {
    StockTransactionCreateValidator validator;
    StockTransactionMapper stockTransactionMapper;
    StockTransactionItemMapper stockTransactionItemMapper;
    StockTransactionRepository stockTransactionRepository;
    StockTransactionHistoryRepository stockTransactionHistoryRepository;
    InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public StockTransactionResponse create(StockTransactionRequest stockTransactionRequest) {
        StockTransactionResult result = validator.validate(stockTransactionRequest);

        StockTransaction stockTransaction = stockTransactionMapper.toStockTransaction(result);
        List<StockTransactionItem> stockTransactionItems = stockTransactionItemMapper
                .toStockTransactionItems(result.items())
                .stream()
                .toList();


        stockTransaction.addStockTransactionItems(stockTransactionItems);

        StockTransaction saved = stockTransactionRepository.save(stockTransaction);

        StockTransactionHistory history = new StockTransactionHistory();
        history.setStockTransaction(saved);
        history.setAssignedTo(saved.getAssignedTo());
        history.setFromStatus(saved.getStatus());
        history.setToStatus(saved.getStatus());
        history.setNote("Initial creation");
        saved.getStockTransactionHistories().add(history);

        stockTransactionHistoryRepository.save(history);

        return stockTransactionMapper.toStockTransactionResponse(saved);
    }

    @Override
    @Transactional(timeout = 10)
    public StockTransactionResponse changeStatus(UUID id, @NonNull StockTransactionStatusRequest request) {
        StockTransaction stockTransaction = stockTransactionRepository.findNotDeletedById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STOCK_TRANSACTION_NOT_FOUND, id.toString()));

        StockTransactionStatus currentStatus = stockTransaction.getStatus();
        StockTransactionStatus nextStatus = request.status();
        currentStatus.validateTransitionTo(nextStatus);

        stockTransaction.setStatus(nextStatus);

        for (StockTransactionItem item : stockTransaction.getStockTransactionItems()) {
            long quantityChange = resolveQuantityChange(stockTransaction, item);

            Inventory inventory = inventoryRepository.findLockedByVariantIdAndWarehouseIdAndDeletedAtIsNull(
                    item.getVariant().getId(),
                    stockTransaction.getWarehouse().getId()
            );

            switch (nextStatus) {
                case DRAFT, PENDING, PROCESSING -> {
                    // No inventory mutation is required when moving into non-final workflow states.
                }
                case CANCELLED -> applyCancelledStatus(currentStatus, quantityChange, inventory, item.getVariant());
                case CONFIRMED -> applyConfirmedStatus(quantityChange, inventory, item.getVariant());
                case COMPLETED -> applyCompletedStatus(stockTransaction, quantityChange, inventory, item.getVariant(), stockTransaction.getWarehouse());
            }
        }

        StockTransactionHistory history = new StockTransactionHistory();
        history.setStockTransaction(stockTransaction);
        history.setAssignedTo(stockTransaction.getAssignedTo());
        history.setFromStatus(currentStatus);
        history.setToStatus(nextStatus);
        history.setNote(request.note());
        history.setReason(request.reason());
        stockTransaction.getStockTransactionHistories().add(history);

        stockTransactionHistoryRepository.save(history);
        StockTransaction saved = stockTransactionRepository.save(stockTransaction);

        return stockTransactionMapper.toStockTransactionResponse(saved);
    }

    @Override
    public StockTransactionResponse getById(UUID id) {
        return stockTransactionMapper
                .toStockTransactionResponse(
                        stockTransactionRepository.findNotDeletedById(id)
                                .orElseThrow(() ->
                                        new AppException(ErrorCode.STOCK_TRANSACTION_NOT_FOUND, id.toString())
                                )
                );
    }

    private void applyCancelledStatus(
            StockTransactionStatus currentStatus,
            long quantityChange,
            Inventory inventory,
            Variant variant
    ) {
        if (quantityChange < 0 && (
                        currentStatus == StockTransactionStatus.CONFIRMED ||
                                currentStatus == StockTransactionStatus.PROCESSING
        )) {
            validateInventory(inventory, variant);
            inventory.setReservedQuantity(inventory.getReservedQuantity() + quantityChange);
            inventoryRepository.save(inventory);
        }
    }

    private void applyConfirmedStatus(
            long quantityChange,
            Inventory inventory,
            Variant variant
    ) {
        validateInventory(inventory, variant);

        long beforeQuantity = inventory.getAvailableQuantity();
        long afterQuantity = beforeQuantity + quantityChange;

        if (afterQuantity < 0) {
            throw new AppException(
                    ErrorCode.INVENTORY_INSUFFICIENT_QUANTITY,
                    variant.getId().toString()
            );
        }

        if (quantityChange < 0) {
            inventory.setReservedQuantity(inventory.getReservedQuantity() - quantityChange);
        }

        inventoryRepository.save(inventory);
    }

    private void validateInventory(Inventory inventory, Variant variant) {
        if (inventory == null) {
            throw new AppException(
                    ErrorCode.INVENTORY_NOT_FOUND,
                    variant.getId().toString()
            );
        }
    }

    private void applyCompletedStatus(
            StockTransaction stockTransaction,
            long quantityChange,
            Inventory inventory,
            Variant variant,
            Warehouse warehouse
    ) {
        if (inventory == null) {
            if (quantityChange < 0) {
                throw new AppException(
                        ErrorCode.INVENTORY_NOT_FOUND
                );
            }

            inventoryRepository.insertMissingInventoryIfAbsent(
                    variant.getId(),
                    warehouse.getId(),
                    AuditConstant.SYSTEM_USER_ID
            );

            inventory = inventoryRepository.findLockedByVariantIdAndWarehouseIdAndDeletedAtIsNull(
                    variant.getId(),
                    warehouse.getId()
            );

            if (inventory == null) {
                throw new AppException(
                        ErrorCode.INVENTORY_NOT_FOUND,
                        variant.getId().toString()
                );
            }
        }

        long beforeQuantity = inventory.getAvailableQuantity();
        long afterQuantity = beforeQuantity + quantityChange;

        if (afterQuantity < 0) {
            throw new AppException(
                    ErrorCode.INVENTORY_INSUFFICIENT_QUANTITY,
                    variant.getId().toString()
            );
        }

        inventory.setQuantity(afterQuantity);

        InventoryMovement movement = new InventoryMovement();
        movement.setInventory(inventory);
        movement.setStockTransaction(stockTransaction);
        movement.setQuantityChange(quantityChange);
        movement.setBeforeQuantity(beforeQuantity);
        movement.setAfterQuantity(afterQuantity);
        stockTransaction.getInventoryMovements().add(movement);
    }

    private long resolveQuantityChange(@NonNull StockTransaction stockTransaction, @NonNull StockTransactionItem item) {
        long quantity = item.getQuantity();

        return switch (stockTransaction.getType()) {
            case IMPORT -> quantity;
            case EXPORT -> -quantity;
            case ADJUSTMENT -> item.getAdjustmentType() == AdjustmentType.INCREASE ? quantity : -quantity;
        };
    }
}
