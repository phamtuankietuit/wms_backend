package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.Warehouse;
import com.kit.wmsbackend.feature.stocktransaction.dto.WarehouseResponseForStockTransaction;
import com.kit.wmsbackend.feature.warehouse.dto.WarehouseRequest;
import com.kit.wmsbackend.feature.warehouse.dto.WarehouseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface WarehouseMapper {
    Warehouse toWarehouse(WarehouseRequest warehouseRequest);

    WarehouseResponse toWarehouseResponse(Warehouse warehouse);

    void updateWarehouse(@MappingTarget Warehouse warehouse, WarehouseRequest warehouseRequest);

    WarehouseResponseForStockTransaction toWarehouseResponseForStockTransaction(Warehouse warehouse);
}
