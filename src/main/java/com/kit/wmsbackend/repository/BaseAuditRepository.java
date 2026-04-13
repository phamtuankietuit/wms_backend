package com.kit.wmsbackend.repository;

import com.kit.wmsbackend.entity.BaseAuditEntity;
import com.kit.wmsbackend.exception.ResourceNotFoundException;
import com.kit.wmsbackend.specification.BaseSpecification;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BaseAuditRepository<T extends BaseAuditEntity>
        extends JpaRepository<T, UUID>, JpaSpecificationExecutor<T> {

    default List<T> findAllNotDeleted() {
        return findAll(BaseSpecification.notDeleted());
    }

    default List<T> findAllDeleted() {
        return findAll(BaseSpecification.deleted());
    }

    default Optional<T> findNotDeletedById(UUID id) {
        return findOne(
                Specification
                        .where(BaseSpecification.<T>notDeleted())
                        .and((root, query, cb) ->
                                cb.equal(root.get("id"), id))
        );
    }

    default Optional<T> findDeletedById(UUID id) {
        return findOne(
                Specification
                        .where(BaseSpecification.<T>deleted())
                        .and((root, query, cb) ->
                                cb.equal(root.get("id"), id))
        );
    }

    @Transactional
    default void softDelete(@NonNull T entity) {
        findNotDeletedById(entity.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Entity", "id", entity.getId()));

        entity.setDeletedAt(Instant.now());
        this.save(entity);
    }

    @Transactional
    default void softDeleteById(@NonNull UUID id) {
        T entity = findNotDeletedById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entity", "id" ,id));

        entity.setDeletedAt(Instant.now());
        save(entity);
    }

    @Transactional
    default T restore(@NonNull T entity) {
        findDeletedById(entity.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Entity", "id", entity.getId()));

        entity.setDeletedAt(null);
        return save(entity);
    }

    @Transactional
    default T restoreById(@NonNull UUID id) {
        T entity = findDeletedById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entity", "id" ,id));

        entity.setDeletedAt(null);
        return save(entity);
    }
}
