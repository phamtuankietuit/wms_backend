package com.kit.wmsbackend.feature.product.repository;

import com.kit.wmsbackend.entity.ProductAttributeId;
import com.kit.wmsbackend.entity.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, ProductAttributeId> {
}