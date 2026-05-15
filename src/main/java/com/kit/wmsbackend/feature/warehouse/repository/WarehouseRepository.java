package com.kit.wmsbackend.feature.warehouse.repository;

import com.kit.wmsbackend.entity.Warehouse;
import com.kit.wmsbackend.repository.BaseAuditRepository;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarehouseRepository extends BaseAuditRepository<Warehouse> {
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, UUID id);
    boolean existsByIdAndDeletedAtIsNullAndIsActiveTrue(UUID id);

    @Query("""
            SELECT w
            FROM Warehouse w
            WHERE w.id IN :ids
            AND w.deletedAt IS NULL
            AND w.isActive = true
            """)
    List<Warehouse> findAllNotDeletedAndActive(@Param("ids") Collection<UUID> ids);

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

