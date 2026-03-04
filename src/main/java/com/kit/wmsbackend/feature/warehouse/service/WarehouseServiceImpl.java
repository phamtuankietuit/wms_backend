package com.kit.wmsbackend.feature.warehouse.service;

import com.kit.wmsbackend.entity.Warehouse;
import com.kit.wmsbackend.feature.warehouse.repository.WarehouseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;

    @Override
    public List<Warehouse> findAll() {
        return warehouseRepository.findAll();
    }

    @Override
    public Warehouse findById(String id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with id: " + id));
    }

    @Override
    @Transactional
    public Warehouse create(Warehouse warehouse) {
        warehouseRepository.findByCode(warehouse.getCode()).ifPresent(existing -> {
            throw new IllegalArgumentException("Warehouse code already exists: " + warehouse.getCode());
        });
        return warehouseRepository.save(warehouse);
    }

    @Override
    @Transactional
    public Warehouse update(String id, Warehouse warehouse) {
        Warehouse existing = findById(id);

        warehouseRepository.findByCode(warehouse.getCode()).ifPresent(found -> {
            if (!found.getId().equals(id)) {
                throw new IllegalArgumentException("Warehouse code already exists: " + warehouse.getCode());
            }
        });

        existing.setCode(warehouse.getCode());
        existing.setName(warehouse.getName());
        return warehouseRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Warehouse existing = findById(id);
        warehouseRepository.delete(existing);
    }
}

