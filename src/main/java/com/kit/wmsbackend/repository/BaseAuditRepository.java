package com.kit.wmsbackend.repository;

import com.kit.wmsbackend.entity.BaseAuditEntity;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.specification.BaseSpecification;
import com.kit.wmsbackend.utils.SecurityUtils;
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
        entity.setDeletedAt(Instant.now());
        entity.setDeletedBy(SecurityUtils.getCurrentUserIdOrSystem("soft delete operation"));
        this.save(entity);
    }

    @Transactional
    default void softDeleteById(@NonNull UUID id) {
        T entity = findNotDeletedById(id)
                .orElseThrow(() -> new AppException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Entity [" + id + "]"
                ));

        softDelete(entity);
    }

    @Transactional
    default void softDeleteAll(@NonNull Iterable<T> entities) {
        Instant now = Instant.now();
        UUID deletedBy = SecurityUtils.getCurrentUserIdOrSystem("bulk soft delete operation");

        for (T entity : entities) {
            entity.setDeletedAt(now);
            entity.setDeletedBy(deletedBy);
        }

        saveAll(entities);
    }

    @Transactional
    default T restore(@NonNull T entity) {
        entity.setDeletedAt(null);
        entity.setDeletedBy(null);
        return save(entity);
    }

    @Transactional
    default T restoreById(@NonNull UUID id) {
        T entity = findDeletedById(id)
                .orElseThrow(() -> new AppException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Entity [" + id + "]"
                ));

        return restore(entity);
    }
}
