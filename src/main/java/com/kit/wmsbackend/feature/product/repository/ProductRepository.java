package com.kit.wmsbackend.feature.product.repository;

import com.kit.wmsbackend.entity.Product;
import com.kit.wmsbackend.repository.BaseAuditRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends BaseAuditRepository<Product> {
	boolean existsByCode(String code);

	@Query("""
			SELECT DISTINCT p
			FROM Product p
			LEFT JOIN FETCH p.productAttributes pa
			LEFT JOIN FETCH pa.attribute a
			WHERE p.id = :id
			AND p.deletedAt IS NULL
			""")
	Optional<Product> findDetailById(@Param("id") UUID id);

	@Query("""
			SELECT DISTINCT p
			FROM Product p
			LEFT JOIN FETCH p.variants v
			WHERE p.id IN :ids
			AND p.deletedAt IS NULL
			""")
	List<Product> findAllNotDeletedWithVariantsByIdIn(@Param("ids") Collection<UUID> ids);

	@Query("""
			SELECT DISTINCT p
			FROM Product p
			LEFT JOIN FETCH p.variants v
			WHERE p.id IN :ids
			AND p.deletedAt IS NOT NULL
			""")
	List<Product> findAllDeletedWithVariantsByIdIn(@Param("ids") Collection<UUID> ids);
}
