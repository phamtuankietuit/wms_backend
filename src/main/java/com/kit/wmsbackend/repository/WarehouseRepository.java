package com.kit.wmsbackend.repository;

import com.kit.wmsbackend.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WarehouseRepository extends JpaRepository<Warehouse, String> {
    Optional<Warehouse> findByCode(String code);

    boolean existsByCode(String code);
}
