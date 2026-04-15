package com.kit.wmsbackend.feature.warehouse.service;

import com.kit.wmsbackend.feature.warehouse.dto.WarehouseRequest;
import com.kit.wmsbackend.feature.warehouse.dto.WarehouseResponse;
import jakarta.validation.Valid;

public interface WarehouseService {
    WarehouseResponse create(@Valid WarehouseRequest warehouseRequest);
}

