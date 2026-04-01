package com.kit.wmsbackend.feature.warehouse.service;

import com.kit.wmsbackend.entity.Warehouse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {
    @Override
    public List<Warehouse> findAll() {
        return List.of();
    }

    @Override
    public Warehouse findById(UUID id) {
        return null;
    }

    @Override
    public Warehouse create(Warehouse warehouse) {
        return null;
    }

    @Override
    public Warehouse update(UUID id, Warehouse warehouse) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
