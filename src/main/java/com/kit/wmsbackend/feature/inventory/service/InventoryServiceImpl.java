package com.kit.wmsbackend.feature.inventory.service;

import com.kit.wmsbackend.entity.Inventory;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.inventory.repository.InventoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryServiceImpl implements InventoryService{
    InventoryRepository inventoryRepository;

    @Override
    public boolean isAvailableQuantity(UUID inventoryId, Integer quantity) {
        Inventory inventory = inventoryRepository.findNotDeletedById(inventoryId)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND, inventoryId.toString()));

        return inventory.getAvailableQuantity() >= quantity;
    }

    @Override
    public Long getAvailableQuantity(UUID variantId, UUID warehouseId) {
        Inventory inventory = inventoryRepository
                .findByVariantIdAndWarehouseIdAndDeletedAtIsNull(variantId, warehouseId);

        if (inventory == null) {
            return 0L;
        } else {
            return inventory.getAvailableQuantity();
        }
    }
}
