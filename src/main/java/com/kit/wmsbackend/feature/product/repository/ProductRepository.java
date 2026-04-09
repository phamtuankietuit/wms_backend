package com.kit.wmsbackend.feature.product.repository;

import com.kit.wmsbackend.entity.Product;
import com.kit.wmsbackend.repository.BaseAuditRepository;

public interface ProductRepository extends BaseAuditRepository<Product> {
	boolean existsByCode(String code);
	boolean existsByCodeAndIdNot(String code, java.util.UUID id);
}
