package com.kit.wmsbackend.feature.refreshtoken.repository;

import com.kit.wmsbackend.entity.RefreshToken;
import com.kit.wmsbackend.repository.BaseAuditRepository;
import com.kit.wmsbackend.specification.BaseSpecification;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends BaseAuditRepository<RefreshToken> {
     default Optional<RefreshToken> findNotDeletedByJti(String jti) {
          return findOne(
                  Specification
                          .where(BaseSpecification.<RefreshToken>notDeleted())
                          .and((root, query, cb) ->
                                  cb.equal(root.get("jti"), jti))
          );
     }

     @Lock(LockModeType.PESSIMISTIC_WRITE)
     @Query("""
             SELECT rt
             FROM RefreshToken rt
             WHERE rt.user.id = :userId
             AND rt.jti = :jti
             AND rt.deletedAt IS NULL
             AND rt.expiresAt > :now
             """)
     Optional<RefreshToken> findValidByUserIdAndJtiForUpdate(
             @Param("userId") UUID userId,
             @Param("jti") String jti,
             @Param("now") Instant now
     );
}
