package com.kit.wmsbackend.feature.warehouse.repository;

import com.kit.wmsbackend.entity.Warehouse;
import com.kit.wmsbackend.repository.BaseAuditRepository;

import java.util.Optional;
import java.util.UUID;

public interface WarehouseRepository extends BaseAuditRepository<Warehouse> {
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, UUID id);
    boolean existsByIdAndDeletedAtIsNullAndIsActiveTrue(UUID id);

    Optional<Warehouse> findByIdAndDeletedAtIsNullAndIsActiveTrue(UUID id);
}

