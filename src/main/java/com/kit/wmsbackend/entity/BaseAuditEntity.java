package com.kit.wmsbackend.entity;

import com.kit.wmsbackend.service.SoftDeleteFilterManager;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@FilterDef(
    name = SoftDeleteFilterManager.FILTER_NAME,
    parameters = @ParamDef(name = SoftDeleteFilterManager.PARAM_INCLUDE_DELETED, type = Boolean.class),
    defaultCondition = "deleted_at IS NULL"
)
@Filter(
    name = SoftDeleteFilterManager.FILTER_NAME,
    condition = "(:includeDeleted = true OR deleted_at IS NULL)"
)
public abstract class BaseAuditEntity extends BaseEntity {
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Transient
    public boolean isDeleted() {
        return deletedAt != null;
    }
}

