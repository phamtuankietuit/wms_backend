package com.kit.wmsbackend.feature.inventory.service;

import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.feature.inventory.dto.InventoryResponse;

import java.util.List;
import java.util.UUID;

public interface InventoryService {
    boolean isAvailableQuantity(UUID inventoryId, Integer quantity);
    Long getAvailableQuantity(UUID variantId, UUID warehouseId);
    ListResponse<List<InventoryResponse>> list(ListRequest listRequest);
}
