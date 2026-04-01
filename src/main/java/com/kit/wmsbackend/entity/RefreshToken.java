package com.kit.wmsbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_user_active", columnList = "user_id, deleted_at"),
    @Index(name = "idx_expires_at", columnList = "expires_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE refresh_tokens SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class RefreshToken extends BaseAuditEntity {
    @Column(unique = true, nullable = false)
    String jti;

    @Column(unique = true, nullable = false)
    String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(length = 500)
    String userAgent;

    String ipAddress;

    @Column(nullable = false)
    LocalDateTime expiresAt;

    LocalDateTime lastUsedAt;
}
