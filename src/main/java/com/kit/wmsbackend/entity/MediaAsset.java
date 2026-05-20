package com.kit.wmsbackend.entity;

import com.kit.wmsbackend.enums.MediaOwnerType;
import com.kit.wmsbackend.enums.MediaResourceType;
import com.kit.wmsbackend.enums.MediaResourceTypeConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "media_assets",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_media_assets_public_id", columnNames = "public_id")
        },
        indexes = {
                @Index(name = "idx_media_assets_owner", columnList = "owner_type, owner_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MediaAsset extends BaseAuditEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false, length = 50)
    private MediaOwnerType ownerType;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "public_id", nullable = false)
    private String publicId;

    @Column(name = "secure_url", nullable = false, length = 500)
    private String secureUrl;

    @Convert(converter = MediaResourceTypeConverter.class)
    @Column(name = "resource_type", nullable = false, length = 50)
    private MediaResourceType resourceType;

    @Column(length = 50)
    private String format;

    private Long bytes;

    private Integer width;

    private Integer height;

    @Column(name = "original_filename")
    private String originalFilename;

    @Column(name = "sort_order", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer sortOrder = 0;

    @Column(name = "is_primary", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isPrimary = false;
}
