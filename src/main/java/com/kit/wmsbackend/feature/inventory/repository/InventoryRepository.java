package com.kit.wmsbackend.feature.inventory.repository;

import com.kit.wmsbackend.entity.Inventory;
import com.kit.wmsbackend.repository.BaseAuditRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.UUID;

public interface InventoryRepository extends BaseAuditRepository<Inventory> {
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

    Inventory findByVariantIdAndWarehouseIdAndDeletedAtIsNull(UUID variantId, UUID warehouseId);
}
