package com.kit.wmsbackend.feature.stocktransaction.service;

import com.kit.wmsbackend.assembler.ListResponseAssembler;
import com.kit.wmsbackend.constant.AuditConstant;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.entity.*;
import com.kit.wmsbackend.enums.*;
import com.kit.wmsbackend.feature.stocktransaction.dto.*;
import com.kit.wmsbackend.feature.inventory.repository.InventoryRepository;
import com.kit.wmsbackend.feature.stocktransaction.listqueryfieldconfig.StockTransactionListQueryFieldConfig;
import com.kit.wmsbackend.feature.stocktransaction.repository.StockTransactionRepository;
import com.kit.wmsbackend.feature.stocktransactionhistory.dto.StockTransactionHistoryRequest;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.stocktransactionhistory.service.StockTransactionHistoryService;
import com.kit.wmsbackend.mapper.StockTransactionItemMapper;
import com.kit.wmsbackend.mapper.StockTransactionMapper;
import com.kit.wmsbackend.service.CodeGenerator;
import com.kit.wmsbackend.service.QueryService;
import com.kit.wmsbackend.validator.StockTransactionValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StockTransactionServiceImpl implements StockTransactionService {
    StockTransactionValidator validator;
    StockTransactionMapper stockTransactionMapper;
    StockTransactionItemMapper stockTransactionItemMapper;
    StockTransactionRepository stockTransactionRepository;
    InventoryRepository inventoryRepository;
    CodeGenerator codeGenerator;
    ListResponseAssembler listResponseAssembler;
    QueryService<StockTransaction> queryService;
    StockTransactionListQueryFieldConfig listQueryFieldConfig;
    StockTransactionHistoryService stockTransactionHistoryService;
    StockTransactionItemService stockTransactionItemService;

    @Override
    @Transactional
    public StockTransactionResponse create(StockTransactionRequest stockTransactionRequest) {
        StockTransactionResult result = validator.validateForCreate(stockTransactionRequest);

        StockTransaction stockTransaction = stockTransactionMapper.toStockTransaction(result);
        stockTransaction.setCode(codeGenerator.generateForStockTransaction(SequenceType.valueOf(result.type().name())));
        List<StockTransactionItem> stockTransactionItems = stockTransactionItemMapper
                .toStockTransactionItems(result.items())
                .stream()
                .toList();


        stockTransaction.addStockTransactionItems(stockTransactionItems);

        StockTransaction saved = stockTransactionRepository.save(stockTransaction);

        stockTransactionHistoryService.logHistory(
                new StockTransactionHistoryRequest(
                        saved,
                        saved.getStatus(),
                        saved.getStatus(),
                        saved.getAssignedTo(),
                        "Initial creation",
                        null
                )
        );

        return stockTransactionMapper.toStockTransactionResponse(saved);
    }

    @Override
    @Transactional(timeout = 10)
    public StockTransactionResponse updateForDraft(UUID id, @NonNull StockTransactionUpdateForDraftRequest request) {
        StockTransaction stockTransaction = stockTransactionRepository.findNotDeletedById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STOCK_TRANSACTION_NOT_FOUND, id.toString()));

        StockTransactionResult result = validator.validateForDraftUpdate(stockTransaction, request);

        stockTransaction.setAssignedTo(result.assignedTo());
        stockTransaction.setNote(result.note());

        List<StockTransactionItem> existingItems = new ArrayList<>(stockTransaction.getStockTransactionItems());
        stockTransaction.removeStockTransactionItems(existingItems);

        List<StockTransactionItem> updatedItems = new ArrayList<>(
                stockTransactionItemMapper.toStockTransactionItems(result.items())
        );
        stockTransaction.addStockTransactionItems(updatedItems);
        StockTransaction saved = stockTransactionRepository.save(stockTransaction);

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
                case CONFIRMED -> applyConfirmedStatus(quantityChange, inventory, item.getVariant(), stockTransaction.getWarehouse());
                case COMPLETED -> applyCompletedStatus(stockTransaction, quantityChange, inventory, item.getVariant(), stockTransaction.getWarehouse());
            }
        }

        StockTransaction saved = stockTransactionRepository.save(stockTransaction);

        stockTransactionHistoryService.logHistory(
                new StockTransactionHistoryRequest(
                        saved,
                        currentStatus,
                        nextStatus,
                        saved.getAssignedTo(),
                        request.note(),
                        request.reason()
                )
        );

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

    @Override
    public ListResponse<List<StockTransactionResponse>> list(ListRequest listRequest) {
        return listResponseAssembler.toListResponse(
                queryService
                        .list(
                                listQueryFieldConfig,
                                stockTransactionRepository,
                                listRequest,
                                false,
                                true
                        )
                        .map(stockTransactionMapper::toStockTransactionResponse),
                listRequest.sort(),
                listRequest.filters()
        );
    }

    @Override
    public ListResponse<List<StockTransactionItemResponse>> listItemByStockTransactionId(
            UUID stockTransactionId,
            StockTransactionItemListRequest request
    ) {
        if (stockTransactionRepository.existsById(stockTransactionId)) {
            return stockTransactionItemService.listByStockTransactionId(stockTransactionId, request);
        }

        throw new AppException(ErrorCode.STOCK_TRANSACTION_NOT_FOUND, stockTransactionId.toString());
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
            Variant variant,
            Warehouse warehouse
    ) {
        if (inventory == null) {
            if (quantityChange < 0) {
                throw new AppException(
                        ErrorCode.INVENTORY_NOT_FOUND,
                        variant.getId().toString()
                );
            }
            inventory = insertMissingAndLoadInventory(variant, warehouse);
        }

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
                        ErrorCode.INVENTORY_NOT_FOUND,
                        variant.getId().toString()
                );
            }
            inventory = insertMissingAndLoadInventory(variant, warehouse);
        }

        validateInventory(inventory, variant);

        long beforeQuantity = inventory.getAvailableQuantity();
        long afterQuantity = beforeQuantity + quantityChange;

        if (afterQuantity < 0) {
            throw new AppException(
                    ErrorCode.INVENTORY_INSUFFICIENT_QUANTITY,
                    variant.getId().toString()
            );
        }

        inventory.setQuantity(afterQuantity);

        if (quantityChange < 0) {
            inventory.setReservedQuantity(inventory.getReservedQuantity() + quantityChange);
        }

        InventoryMovement movement = new InventoryMovement();
        movement.setInventory(inventory);
        movement.setStockTransaction(stockTransaction);
        movement.setQuantityChange(quantityChange);
        movement.setBeforeQuantity(beforeQuantity);
        movement.setAfterQuantity(afterQuantity);
        stockTransaction.getInventoryMovements().add(movement);
    }

    private Inventory insertMissingAndLoadInventory(
            @NonNull Variant variant,
            @NonNull Warehouse warehouse
    ) {
            inventoryRepository.insertMissingInventoryIfAbsent(
                    variant.getId(),
                    warehouse.getId(),
                    AuditConstant.SYSTEM_USER_ID
            );

            return inventoryRepository.findLockedByVariantIdAndWarehouseIdAndDeletedAtIsNull(
                    variant.getId(),
                    warehouse.getId()
            );
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
