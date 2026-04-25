package com.kit.wmsbackend.validator;

import com.kit.wmsbackend.entity.Inventory;
import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.entity.Variant;
import com.kit.wmsbackend.entity.Warehouse;
import com.kit.wmsbackend.enums.AdjustmentType;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.enums.StockTransactionType;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.inventory.repository.InventoryRepository;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionItemRequest;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionItemResult;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionRequest;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionResult;
import com.kit.wmsbackend.feature.user.repository.UserRepository;
import com.kit.wmsbackend.feature.variant.repository.VariantRepository;
import com.kit.wmsbackend.feature.warehouse.repository.WarehouseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StockTransactionCreateValidator {
    WarehouseRepository warehouseRepository;
    InventoryRepository inventoryRepository;
    VariantRepository variantRepository;
    UserRepository userRepository;

    public StockTransactionResult validate(@NonNull StockTransactionRequest req) {
        validateAllAdjustmentType(req.type(), req.items());
        validateWarehouse(req.warehouseId());
        validateAssignedTo(req.assignedTo());

        List<StockTransactionItemResult> itemsResult = validateAndLoadItems(req);

        Warehouse warehouse = warehouseRepository.getReferenceById(req.warehouseId());
        User assignedTo = userRepository.getReferenceById(req.assignedTo());

        return new StockTransactionResult(
                warehouse,
                assignedTo,
                req.type(),
                req.note(),
                itemsResult
        );
    }

    private void validateWarehouse(UUID warehouseId) {
         if (!warehouseRepository.existsByIdAndDeletedAtIsNullAndIsActiveTrue(warehouseId)) {
             throw new AppException(ErrorCode.WAREHOUSE_NOT_FOUND, warehouseId.toString());
         }
    }

    private void validateAssignedTo(UUID assignedTo) {
        if (!userRepository.existsByIdAndDeletedAtIsNull(assignedTo)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, assignedTo.toString());
        }
    }

    private @NonNull @Unmodifiable List<StockTransactionItemResult> validateAndLoadItems(
            @NonNull StockTransactionRequest req
    ) {
        List<StockTransactionItemRequest> items = req.items();
        Map<UUID, StockTransactionItemRequest> itemRequestMap = new HashMap<>();

        for (StockTransactionItemRequest item : items) {
            if (itemRequestMap.putIfAbsent(item.variantId(), item) != null) {
                throw new AppException(ErrorCode.VARIANT_DUPLICATE, item.variantId().toString());
            }
        }

        Map<UUID, Variant> variantMap =
                switch (req.type()) {
            case IMPORT -> getVariantMapForImport(itemRequestMap.keySet());
            case EXPORT, ADJUSTMENT -> getVariantMapForExportAndAdjustment(req.warehouseId(), itemRequestMap.keySet());
        };

        for (UUID id : itemRequestMap.keySet()) {
            if (!variantMap.containsKey(id)) {
                throw new AppException(ErrorCode.VARIANT_NOT_FOUND, id.toString());
            }
        }

        return items
                .stream()
                .map(item -> new StockTransactionItemResult(
                        variantMap.get(item.variantId()),
                        item.quantity(),
                        item.adjustmentType()
                ))
                .toList();
    }

    private Map<UUID, Variant> getVariantMapForImport(Set<UUID> variantIds) {
        return variantRepository
                .findAllByIdInAndDeletedAtIsNullAndIsActiveTrue(variantIds)
                .stream()
                .collect(Collectors.toMap(Variant::getId, v -> v));
    }

    private Map<UUID, Variant> getVariantMapForExportAndAdjustment(UUID warehouseId, Set<UUID> variantIds) {
        return inventoryRepository
                .findAllValidForCreateStockTransaction(
                        warehouseId,
                        variantIds
                )
                .stream()
                .collect(Collectors.toMap(
                        i -> i.getVariant().getId(),
                        Inventory::getVariant)
                );
    }

    private void validateAllAdjustmentType(
            StockTransactionType type,
            @NonNull List<StockTransactionItemRequest> items
    ) {
        for (StockTransactionItemRequest item : items) {
            if (!isValidAdjustmentType(type, item.adjustmentType())) {
                throw new AppException(
                        ErrorCode.STOCK_TRANSACTION_ITEM_INVALID,
                        "Type " + item.adjustmentType()
                );
            }
        }
    }

    private boolean isValidAdjustmentType(@NonNull StockTransactionType type, AdjustmentType adjustmentType) {
        return switch (type) {
            case IMPORT -> AdjustmentType.INCREASE.equals(adjustmentType);
            case EXPORT -> AdjustmentType.DECREASE.equals(adjustmentType);
            case ADJUSTMENT -> true;
        };
    }
}
