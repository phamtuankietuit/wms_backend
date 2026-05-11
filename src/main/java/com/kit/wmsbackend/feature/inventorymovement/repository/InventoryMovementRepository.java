package com.kit.wmsbackend.feature.inventorymovement.repository;

import com.kit.wmsbackend.entity.InventoryMovement;
import com.kit.wmsbackend.repository.BaseAuditRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;

public interface InventoryMovementRepository extends BaseAuditRepository<InventoryMovement> {
    @NonNull
    @EntityGraph(value = "InventoryMovement.detail", type = EntityGraph.EntityGraphType.FETCH)
    Page<InventoryMovement> findAll(@NonNull Specification<InventoryMovement> spec, @NonNull Pageable pageable);
}
