package com.kit.wmsbackend.feature.warehouse.service;

import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.feature.warehouse.dto.WarehouseRequest;
import com.kit.wmsbackend.feature.warehouse.dto.WarehouseResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface WarehouseService {
    WarehouseResponse create(@Valid WarehouseRequest warehouseRequest);
    WarehouseResponse update(UUID id, @Valid WarehouseRequest warehouseRequest);
    WarehouseResponse getById(UUID id);
    ListResponse<List<WarehouseResponse>> list(@Valid ListRequest listRequest);
    void delete(UUID id);
    WarehouseResponse restore(UUID id);
}

