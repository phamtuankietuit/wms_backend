package com.kit.wmsbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

/**
 * Base audit entity with automatic tracking of creation, modification, and deletion metadata.
 *
 * <p><strong>Equality & Hashing:</strong> Entity identity is based solely on the {@code id} field
 * (inherited from {@link BaseEntity}). Audit fields ({@code createdAt}, {@code updatedAt},
 * {@code deletedAt}, {@code createdBy}, {@code updatedBy}, {@code deletedBy}, and relationship
 * fields {@code creator}, {@code updater}, {@code deleter}) are NOT part of equals/hashCode.
 * This ensures that two entities with the same ID are considered equal regardless of their
 * audit history, which is the correct semantics for JPA managed entities.
 *
 * <p>Relationship fields are also excluded because they represent audit metadata, not business
 * identity. Collections of related entities are never included in equality checks.
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseAuditEntity extends BaseEntity {
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private UUID createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id", insertable = false, updatable = false)
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", referencedColumnName = "id", insertable = false, updatable = false)
    private User updater;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by", referencedColumnName = "id", insertable = false, updatable = false)
    private User deleter;

    @Transient
    public boolean isDeleted() {
        return deletedAt != null;
    }
}

