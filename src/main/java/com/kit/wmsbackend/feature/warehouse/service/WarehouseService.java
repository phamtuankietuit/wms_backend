package com.kit.wmsbackend.feature.warehouse.service;

import com.kit.wmsbackend.entity.Warehouse;

import java.util.List;
import java.util.UUID;

public interface WarehouseService {
    List<Warehouse> findAll();

    Warehouse findById(UUID id);

    Warehouse create(Warehouse warehouse);

    Warehouse update(UUID id, Warehouse warehouse);

    void delete(UUID id);
}

