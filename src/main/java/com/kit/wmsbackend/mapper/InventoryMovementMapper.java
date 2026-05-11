package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.InventoryMovement;
import com.kit.wmsbackend.feature.inventorymovement.dto.InventoryMovementResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface InventoryMovementMapper {
    InventoryMovementResponse toInventoryMovementResponse(InventoryMovement inventoryMovement);
}
