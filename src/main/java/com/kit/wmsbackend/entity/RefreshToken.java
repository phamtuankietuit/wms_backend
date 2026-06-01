package com.kit.wmsbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_user_active", columnList = "user_id, deleted_at"),
    @Index(name = "idx_expires_at", columnList = "expires_at"),
    @Index(name = "idx_refresh_tokens_session_id", columnList = "session_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE refresh_tokens SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class RefreshToken extends BaseAuditEntity {
    @Column(unique = true, nullable = false)
    String jti;

    @Column(nullable = false)
    String sessionId;

    @Column(unique = true, nullable = false)
    String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(length = 500)
    String userAgent;

    String ipAddress;

    @Column(nullable = false)
    Instant expiresAt;

    Instant lastUsedAt;
}
