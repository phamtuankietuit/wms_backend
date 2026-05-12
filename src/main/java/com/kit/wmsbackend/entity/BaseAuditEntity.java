package com.kit.wmsbackend.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(callSuper = true)
public abstract class BaseAuditEntity extends BaseEntity {
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @EqualsAndHashCode.Exclude
    private Instant updatedAt;

    @Column(name = "deleted_at")
    @EqualsAndHashCode.Exclude
    private Instant deletedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    @EqualsAndHashCode.Exclude
    private UUID createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    @EqualsAndHashCode.Exclude
    private UUID updatedBy;

    @Column(name = "deleted_by")
    @EqualsAndHashCode.Exclude
    private UUID deletedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", referencedColumnName = "id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    private User updater;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by", referencedColumnName = "id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    private User deleter;

    @Transient
    public boolean isDeleted() {
        return deletedAt != null;
    }
}

