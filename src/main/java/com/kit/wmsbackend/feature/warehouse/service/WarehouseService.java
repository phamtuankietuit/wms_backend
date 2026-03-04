package com.kit.wmsbackend.feature.warehouse.service;

import com.kit.wmsbackend.entity.Warehouse;

import java.util.List;

public interface WarehouseService {
    List<Warehouse> findAll();

    Warehouse findById(String id);

    Warehouse create(Warehouse warehouse);

    Warehouse update(String id, Warehouse warehouse);

    void delete(String id);
}

