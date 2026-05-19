package com.kit.wmsbackend.feature.stocktransaction.service;

import com.kit.wmsbackend.assembler.ListResponseAssembler;
import com.kit.wmsbackend.constant.AuditConstant;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.entity.*;
import com.kit.wmsbackend.enums.*;
import com.kit.wmsbackend.feature.stocktransaction.comparator.InventoryKeyComparator;
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
import com.kit.wmsbackend.utils.ValidateUtils;
import com.kit.wmsbackend.validator.StockTransactionValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    ValidateUtils validateUtils;

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

        return stockTransactionMapper.toStockTransactionResponse(stockTransaction);
    }

    @Override
    @Transactional(timeout = 10)
    public StockTransactionResponse changeStatus(UUID id, @NonNull StockTransactionStatusRequest request) {
        StockTransaction stockTransaction = stockTransactionRepository.findWithItemsById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STOCK_TRANSACTION_NOT_FOUND, id.toString()));

        return changeStatuses(List.of(stockTransaction), request.status(), request.reason(), request.note())
                .getFirst();
    }

    @Override
    @Transactional(timeout = 10)
    public List<StockTransactionResponse> bulkChangeStatus(@NonNull StockTransactionBulkStatusRequest request) {
        validateBulkStatus(request.nextStatus());

        List<StockTransaction> stockTransactions = stockTransactionRepository.findAllWithItemsByIdIn(request.ids());
        validateUtils.validateEntitiesExist(stockTransactions, request.ids(), ErrorCode.STOCK_TRANSACTION_NOT_FOUND);

        return changeStatuses(stockTransactions, request.nextStatus(), request.reason(), request.note());
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

    private @NonNull @Unmodifiable List<StockTransactionResponse> changeStatuses(
            @NonNull List<StockTransaction> stockTransactions,
            @NonNull StockTransactionStatus nextStatus,
            String reason,
            String note
    ) {
        List<StockTransactionStatusChange> changes = stockTransactions
                .stream()
                .map(stockTransaction -> new StockTransactionStatusChange(
                        stockTransaction,
                        nextStatus
                ))
                .toList();

        validateStatusTransitions(changes);

        Map<InventoryKey, Inventory> inventories = loadLockedInventories(changes);
        validateInventoryChanges(changes, inventories);
        applyInventoryChanges(changes, inventories);

        for (StockTransactionStatusChange change : changes) {
            StockTransaction stockTransaction = change.stockTransaction();
            StockTransactionStatus currentStatus = stockTransaction.getStatus();
            stockTransaction.setStatus(change.nextStatus());

            stockTransactionHistoryService.logHistory(
                    new StockTransactionHistoryRequest(
                            stockTransaction,
                            currentStatus,
                            change.nextStatus(),
                            stockTransaction.getAssignedTo(),
                            note,
                            reason
                    )
            );
        }

        return stockTransactions
                .stream()
                .map(stockTransactionMapper::toStockTransactionResponse)
                .toList();
    }

    private @NonNull Map<InventoryKey, Inventory> loadLockedInventories(
            @NonNull List<StockTransactionStatusChange> changes
    ) {
        Set<InventoryKey> distinctKeys = new HashSet<>();

        for (StockTransactionStatusChange change : changes) {
            if (!requiresInventory(change.nextStatus())) {
                continue;
            }

            StockTransaction stockTransaction = change.stockTransaction();
            for (StockTransactionItem item : stockTransaction.getStockTransactionItems()) {
                distinctKeys.add(inventoryKey(stockTransaction, item));
            }
        }

        List<InventoryKey> sortedKeys = distinctKeys
                .stream()
                .sorted(InventoryKeyComparator.INVENTORY_LOCK_ORDER)
                .toList();

        Map<InventoryKey, Inventory> inventories = new LinkedHashMap<>();

        for (InventoryKey key : sortedKeys) {
            Inventory inventory = inventoryRepository.findLockedByVariantIdAndWarehouseIdAndDeletedAtIsNull(
                    key.variantId(),
                    key.warehouseId()
            );
            inventories.put(key, inventory);
        }

        return inventories;
    }

    private void validateInventoryChanges(
            @NonNull List<StockTransactionStatusChange> changes,
            @NonNull Map<InventoryKey, Inventory> inventories
    ) {
        if (changes.isEmpty()) {
            return;
        }

        switch (changes.getFirst().nextStatus()) {
            case DRAFT, PENDING, PROCESSING, COMPLETED -> {
                // No inventory validation is required when moving into non-inventory workflow states.
            }
            case CONFIRMED -> validateConfirmedStatus(changes, inventories);
            case CANCELLED -> validateCancelledStatus(changes, inventories);
        }
    }

    private void validateConfirmedStatus(
            @NonNull List<StockTransactionStatusChange> changes,
            @NonNull Map<InventoryKey, Inventory> inventories
    ) {
        Map<InventoryKey, Long> reservationDemandByInventory = new HashMap<>();

        for (StockTransactionStatusChange change : changes) {
            StockTransaction stockTransaction = change.stockTransaction();
            for (StockTransactionItem item : stockTransaction.getStockTransactionItems()) {
                long quantityChange = resolveQuantityChange(stockTransaction, item);
                if (quantityChange < 0) {
                    reservationDemandByInventory.merge(
                            inventoryKey(stockTransaction, item),
                            -quantityChange,
                            Long::sum
                    );
                }
            }
        }

        for (Map.Entry<InventoryKey, Long> entry : reservationDemandByInventory.entrySet()) {
            Inventory inventory = inventories.get(entry.getKey());

            if (inventory == null) {
                throw new AppException(
                        ErrorCode.INVENTORY_NOT_FOUND,
                        entry.getKey().variantId().toString()
                );
            }

            if (inventory.getAvailableQuantity() < entry.getValue()) {
                throw new AppException(
                        ErrorCode.INVENTORY_INSUFFICIENT_QUANTITY,
                        entry.getKey().variantId().toString()
                );
            }
        }
    }

    private void validateCancelledStatus(
            @NonNull List<StockTransactionStatusChange> changes,
            @NonNull Map<InventoryKey, Inventory> inventories
    ) {
        Map<InventoryKey, Long> reservationReleaseByInventory = new HashMap<>();

        for (StockTransactionStatusChange change : changes) {
            StockTransaction stockTransaction = change.stockTransaction();
            for (StockTransactionItem item : stockTransaction.getStockTransactionItems()) {
                long quantityChange = resolveQuantityChange(stockTransaction, item);
                if (shouldReleaseReservedQuantity(change.stockTransaction().getStatus(), quantityChange)) {
                    reservationReleaseByInventory.merge(
                            inventoryKey(stockTransaction, item),
                            -quantityChange,
                            Long::sum
                    );
                }
            }
        }

        for (Map.Entry<InventoryKey, Long> entry : reservationReleaseByInventory.entrySet()) {
            Inventory inventory = inventories.get(entry.getKey());
            if (inventory == null) {
                throw new AppException(
                        ErrorCode.INVENTORY_NOT_FOUND,
                        entry.getKey().variantId().toString()
                );
            }
        }
    }


    private void applyInventoryChanges(
            @NonNull List<StockTransactionStatusChange> changes,
            @NonNull Map<InventoryKey, Inventory> inventories
    ) {
        for (StockTransactionStatusChange change : changes) {
            StockTransaction stockTransaction = change.stockTransaction();
            for (StockTransactionItem item : stockTransaction.getStockTransactionItems()) {
                InventoryKey key = inventoryKey(stockTransaction, item);
                Inventory inventory = inventories.get(key);
                long quantityChange = resolveQuantityChange(stockTransaction, item);

                switch (change.nextStatus()) {
                    case DRAFT, PENDING, PROCESSING -> {
                        // No inventory mutation is required when moving into non-final workflow states.
                    }
                    case CANCELLED -> applyCancelledStatus(
                            change.stockTransaction().getStatus(),
                            quantityChange,
                            inventory,
                            item.getVariant()
                    );
                    case CONFIRMED -> applyConfirmedStatus(
                            quantityChange,
                            inventory,
                            item.getVariant(),
                            stockTransaction.getWarehouse()
                    );
                    case COMPLETED -> applyCompletedStatus(
                            stockTransaction,
                            quantityChange,
                            inventory,
                            item.getVariant(),
                            stockTransaction.getWarehouse()
                    );
                }
            }
        }
    }

    private void applyCancelledStatus(
            StockTransactionStatus currentStatus,
            long quantityChange,
            Inventory inventory,
            Variant variant
    ) {
        if (shouldReleaseReservedQuantity(currentStatus, quantityChange)) {
            validateInventory(inventory, variant);
            inventory.setReservedQuantity(inventory.getReservedQuantity() + quantityChange);
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

        Long beforeQuantity = inventory.getAvailableQuantity();
        Long afterQuantity = beforeQuantity + quantityChange;

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

    private boolean shouldReleaseReservedQuantity(
            @NonNull StockTransactionStatus currentStatus,
            long quantityChange
    ) {
        return quantityChange < 0 && (
                currentStatus == StockTransactionStatus.CONFIRMED ||
                        currentStatus == StockTransactionStatus.PROCESSING
        );
    }

    private void validateBulkStatus(StockTransactionStatus status) {
        if (status == StockTransactionStatus.DRAFT) {
            throw new AppException(ErrorCode.STOCK_TRANSACTION_INVALID_STATUS, String.valueOf(status));
        }
    }

    private void validateStatusTransitions(@NonNull List<StockTransactionStatusChange> changes) {
        for (StockTransactionStatusChange change : changes) {
            change.stockTransaction().getStatus().validateTransitionTo(change.nextStatus());
        }
    }

    private void validateInventory(Inventory inventory, Variant variant) {
        if (inventory == null) {
            throw new AppException(
                    ErrorCode.INVENTORY_NOT_FOUND,
                    variant.getId().toString()
            );
        }
    }

    private boolean requiresInventory(@NonNull StockTransactionStatus status) {
        return switch (status) {
            case CONFIRMED, COMPLETED, CANCELLED -> true;
            case DRAFT, PENDING, PROCESSING -> false;
        };
    }

    private InventoryKey inventoryKey(
            @NonNull StockTransaction stockTransaction,
            @NonNull StockTransactionItem item
    ) {
        return new InventoryKey(item.getVariant().getId(), stockTransaction.getWarehouse().getId());
    }
}
