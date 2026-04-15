package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.Warehouse;
import com.kit.wmsbackend.feature.warehouse.dto.WarehouseRequest;
import com.kit.wmsbackend.feature.warehouse.dto.WarehouseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface WarehouseMapper {
    @Mapping(target = "isActive", defaultValue = "true")
    Warehouse toWarehouse(WarehouseRequest warehouseRequest);

    WarehouseResponse toWarehouseResponse(Warehouse warehouse);
}
