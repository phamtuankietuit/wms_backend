package com.kit.wmsbackend.feature.variant.repository;

import com.kit.wmsbackend.entity.Variant;
import com.kit.wmsbackend.repository.BaseAuditRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface VariantRepository extends BaseAuditRepository<Variant> {
	boolean existsBySku(String sku);
	Collection<Variant> findAllByIdInAndDeletedAtIsNullAndIsActiveTrue(Collection<UUID> ids);

	@Query("""
			SELECT v
			FROM Variant v
			JOIN FETCH v.inventories i
			WHERE v.id IN :ids AND v.deletedAt IS NULL AND v.isActive = true
			AND i.warehouse.id = :warehouseId
			""")
	List<Variant> findAllValidByIdIn(Collection<UUID> ids, UUID warehouseId);
}
