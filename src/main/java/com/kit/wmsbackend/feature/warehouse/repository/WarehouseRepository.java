package com.kit.wmsbackend.feature.warehouse.repository;

import com.kit.wmsbackend.entity.Warehouse;
import com.kit.wmsbackend.repository.BaseAuditRepository;

import java.util.Optional;

public interface WarehouseRepository extends BaseAuditRepository<Warehouse> {
    Optional<Warehouse> findByCode(String code);

    boolean existsByCode(String code);
}

