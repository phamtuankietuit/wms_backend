package com.kit.wmsbackend.feature.inventory.service;

import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.feature.inventory.dto.InventoryResponse;
import com.kit.wmsbackend.feature.inventorymovement.dto.InventoryMovementListRequest;
import com.kit.wmsbackend.feature.inventorymovement.dto.InventoryMovementResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface InventoryService {
    boolean isAvailableQuantity(UUID inventoryId, Integer quantity);
    Long getAvailableQuantity(UUID variantId, UUID warehouseId);
    ListResponse<List<InventoryResponse>> list(@Valid ListRequest listRequest);
    InventoryResponse getById(UUID inventoryId);
    ListResponse<List<InventoryMovementResponse>> listMovements(UUID inventoryId, @Valid InventoryMovementListRequest listRequest);
}
