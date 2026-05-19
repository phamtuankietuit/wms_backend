package com.kit.wmsbackend.feature.inventory.repository;

import com.kit.wmsbackend.entity.Inventory;
import com.kit.wmsbackend.repository.BaseAuditRepository;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends BaseAuditRepository<Inventory> {
    @EntityGraph(value = "Inventory.detail", type = EntityGraph.EntityGraphType.FETCH)
    @NonNull
    Page<Inventory> findAll(@NonNull Specification<Inventory> spec, @NonNull Pageable pageable);

    @Override
    @Query("""
            SELECT i
            FROM Inventory i
            JOIN FETCH i.variant v
            JOIN FETCH i.variant.product p
            JOIN FETCH i.warehouse w
            WHERE i.id = :id
            AND i.deletedAt IS NULL
            AND v.deletedAt IS NULL
            AND v.isActive = true
            AND p.deletedAt IS NULL
            AND w.deletedAt IS NULL
            """)
    Optional<Inventory> findNotDeletedById(@Param("id") UUID id);

    @Query("""
            SELECT i
            FROM Inventory i
            JOIN FETCH i.variant v
            WHERE i.warehouse.id = :warehouseId
            AND i.variant.id IN :variantIds
            AND i.deletedAt IS NULL
            AND v.deletedAt IS NULL
            AND v.isActive = true
            """)
    Collection<Inventory> findAllValidForCreateStockTransaction(
            @Param("warehouseId") UUID warehouseId,
            @Param("variantIds") Collection<UUID> variantIds
    );

    @Query("""
            SELECT CASE WHEN COUNT(i) > 0 THEN TRUE ELSE FALSE END
            FROM Inventory i
            JOIN i.variant v
            JOIN v.product p
            WHERE p.id IN :productIds
            AND i.deletedAt IS NULL
            AND i.quantity > 0
            """)
    boolean existsQuantityByProductIds(@Param("productIds") Collection<UUID> productIds);

    @Query("""
            SELECT CASE WHEN COUNT(i) > 0 THEN TRUE ELSE FALSE END
            FROM Inventory i
            JOIN i.variant v
            JOIN v.product p
            WHERE p.id IN :productIds
            AND i.deletedAt IS NULL
            AND i.reservedQuantity > 0
            """)
    boolean existsReservedQuantityByProductIds(@Param("productIds") Collection<UUID> productIds);

    Inventory findByVariantIdAndWarehouseIdAndDeletedAtIsNull(UUID variantId, UUID warehouseId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")
    })
    @Query("""
            SELECT i
            FROM Inventory i
            JOIN FETCH i.variant v
            WHERE i.variant.id = :variantId
            AND i.warehouse.id = :warehouseId
            AND i.deletedAt IS NULL
            AND v.deletedAt IS NULL
            AND v.isActive = true
            """)
    Inventory findLockedByVariantIdAndWarehouseIdAndDeletedAtIsNull(
            @Param("variantId") UUID variantId,
            @Param("warehouseId") UUID warehouseId
    );

    @Modifying
    @Query(value = """
            INSERT INTO inventories (id, created_at, updated_at, deleted_at, created_by, updated_by, deleted_by, version, variant_id, warehouse_id, quantity, reserved_quantity)
            VALUES (gen_random_uuid(), NOW(), NOW(), NULL, :systemId, :systemId, NULL, 0, :variantId, :warehouseId, 0, 0)
            ON CONFLICT (variant_id, warehouse_id) DO NOTHING
            """, nativeQuery = true)
    void insertMissingInventoryIfAbsent(
            @Param("variantId") UUID variantId,
            @Param("warehouseId") UUID warehouseId,
            @Param("systemId") UUID systemId
    );
}
