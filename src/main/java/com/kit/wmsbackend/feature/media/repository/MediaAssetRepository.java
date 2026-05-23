package com.kit.wmsbackend.feature.media.repository;

import com.kit.wmsbackend.entity.MediaAsset;
import com.kit.wmsbackend.enums.MediaOwnerType;
import com.kit.wmsbackend.enums.MediaResourceType;
import com.kit.wmsbackend.repository.BaseAuditRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface MediaAssetRepository extends BaseAuditRepository<MediaAsset> {
    @Query("""
            SELECT m
            FROM MediaAsset m
            WHERE m.ownerType = :ownerType
            AND m.ownerId = :ownerId
            AND m.resourceType = :resourceType
            AND m.deletedAt IS NULL
            ORDER BY m.sortOrder ASC, m.createdAt ASC
            """)
    List<MediaAsset> findActiveByOwner(
            @Param("ownerType") @NonNull MediaOwnerType ownerType,
            @Param("ownerId") @NonNull UUID ownerId,
            @Param("resourceType") @NonNull MediaResourceType resourceType
    );

    @Query("""
            SELECT m
            FROM MediaAsset m
            WHERE m.ownerType = :ownerType
            AND m.ownerId IN :ownerIds
            AND m.isPrimary = true
            AND m.deletedAt IS NULL
            """)
    List<MediaAsset> findActivePrimaryByOwners(
            @Param("ownerType") @NonNull MediaOwnerType ownerType,
            @Param("ownerIds") @NonNull Collection<UUID> ownerIds
    );

    @Modifying
    @Query("""
            DELETE FROM MediaAsset m
            WHERE m.publicId = :publicId
            """)
    void hardDeleteByPublicId(@Param("publicId") String publicId);
}
