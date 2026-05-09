package com.kit.wmsbackend.feature.warehouse.repository;

import com.kit.wmsbackend.entity.Warehouse;
import com.kit.wmsbackend.repository.BaseAuditRepository;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface WarehouseRepository extends BaseAuditRepository<Warehouse> {
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, UUID id);
    boolean existsByIdAndDeletedAtIsNullAndIsActiveTrue(UUID id);

    Optional<Warehouse> findByIdAndDeletedAtIsNullAndIsActiveTrue(UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")
    })
    @Query("""
            SELECT w
            FROM Warehouse w
            WHERE w.id = :id
            AND w.deletedAt IS NULL
            AND w.isActive = true
            """)
    Optional<Warehouse> findLockedByIdAndDeletedAtIsNullAndIsActiveTrue(@Param("id") UUID id);
}

