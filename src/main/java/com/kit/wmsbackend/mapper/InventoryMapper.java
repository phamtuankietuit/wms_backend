package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.Inventory;
import com.kit.wmsbackend.feature.inventory.dto.InventoryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface InventoryMapper {
    InventoryResponse toInventoryResponse(Inventory inventory);
}
