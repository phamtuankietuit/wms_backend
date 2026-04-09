package com.kit.wmsbackend.feature.variant.repository;

import com.kit.wmsbackend.entity.Variant;
import com.kit.wmsbackend.repository.BaseAuditRepository;

public interface VariantRepository extends BaseAuditRepository<Variant> {
	boolean existsBySku(String sku);
	boolean existsBySkuAndIdNot(String sku, java.util.UUID id);
}
