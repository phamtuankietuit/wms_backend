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

    @Query(value = """
            SELECT ranked_media_assets.id,
                   ranked_media_assets.created_at,
                   ranked_media_assets.updated_at,
                   ranked_media_assets.deleted_at,
                   ranked_media_assets.created_by,
                   ranked_media_assets.updated_by,
                   ranked_media_assets.deleted_by,
                   ranked_media_assets.owner_type,
                   ranked_media_assets.owner_id,
                   ranked_media_assets.public_id,
                   ranked_media_assets.secure_url,
                   ranked_media_assets.resource_type,
                   ranked_media_assets.format,
                   ranked_media_assets.bytes,
                   ranked_media_assets.width,
                   ranked_media_assets.height,
                   ranked_media_assets.original_filename,
                   ranked_media_assets.sort_order,
                   ranked_media_assets.is_primary
            FROM (
                SELECT m.*,
                       ROW_NUMBER() OVER (
                           PARTITION BY m.owner_id
                           ORDER BY CASE WHEN m.is_primary = TRUE THEN 0 ELSE 1 END ASC,
                                    m.sort_order ASC,
                                    m.created_at ASC
                       ) AS row_number
                FROM media_assets m
                WHERE m.owner_type = :ownerType
                AND m.owner_id IN (:ownerIds)
                AND m.resource_type = :resourceType
                AND m.deleted_at IS NULL
            ) ranked_media_assets
            WHERE ranked_media_assets.row_number = 1
            """, nativeQuery = true)
    List<MediaAsset> findRepresentativeImagesByOwners(
            @Param("ownerType") @NonNull String ownerType,
            @Param("ownerIds") @NonNull Collection<UUID> ownerIds,
            @Param("resourceType") @NonNull String resourceType
    );

    @Modifying
    @Query("""
            DELETE FROM MediaAsset m
            WHERE m.publicId = :publicId
            """)
    void hardDeleteByPublicId(@Param("publicId") String publicId);
}
