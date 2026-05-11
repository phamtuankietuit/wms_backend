package com.kit.wmsbackend.feature.inventorymovement.service;

import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.feature.inventorymovement.dto.InventoryMovementListRequest;
import com.kit.wmsbackend.feature.inventorymovement.dto.InventoryMovementResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface InventoryMovementService {
    ListResponse<List<InventoryMovementResponse>> listByInventoryId(UUID inventoryId, @Valid InventoryMovementListRequest listRequest);
}
