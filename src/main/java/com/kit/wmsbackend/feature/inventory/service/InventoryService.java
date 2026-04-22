package com.kit.wmsbackend.feature.inventory.service;

import java.util.UUID;

public interface InventoryService {
    boolean isAvailableQuantity(UUID inventoryId, Integer quantity);
    Long getAvailableQuantity(UUID variantId, UUID warehouseId);
}
