package com.kit.wmsbackend.feature.warehouse.repository;

import com.kit.wmsbackend.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {
    Optional<Warehouse> findByCode(String code);

    boolean existsByCode(String code);
}

