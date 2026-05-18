package com.kit.wmsbackend.repository;

import com.kit.wmsbackend.entity.BaseAuditEntity;
import com.kit.wmsbackend.entity.BaseEntity_;
import com.kit.wmsbackend.specification.BaseSpecification;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.time.Instant;
import java.util.*;

@NoRepositoryBean
public interface BaseAuditRepository<T extends BaseAuditEntity>
        extends JpaRepository<T, UUID>, JpaSpecificationExecutor<T> {

    default List<T> findAllNotDeleted(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        return findAll(
                Specification.allOf(
                        BaseSpecification.notDeleted(),
                        BaseSpecification.fieldIn(BaseEntity_.id, ids)
                )
        );
    }

    default List<T> findAllDeleted(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        return findAll(
                Specification.allOf(
                        BaseSpecification.deleted(),
                        BaseSpecification.fieldIn(BaseEntity_.id, ids)
                )
        );
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
    default void softDelete(@NonNull T entity, @NonNull UUID deletedBy) {
        entity.setDeletedAt(Instant.now());
        entity.setDeletedBy(deletedBy);
    }

    @Transactional
    default void softDeleteAll(@NonNull Iterable<T> entities, @NonNull UUID deletedBy) {
        Instant now = Instant.now();

        for (T entity : entities) {
            entity.setDeletedAt(now);
            entity.setDeletedBy(deletedBy);
        }
    }

    @Transactional
    default T restore(@NonNull T entity) {
        entity.setDeletedAt(null);
        entity.setDeletedBy(null);
        return entity;
    }

    @Transactional
    default List<T> restoreAll(@NonNull List<T> entities) {
        for (T entity : entities) {
            entity.setDeletedAt(null);
            entity.setDeletedBy(null);
        }
        return entities;
    }
}
