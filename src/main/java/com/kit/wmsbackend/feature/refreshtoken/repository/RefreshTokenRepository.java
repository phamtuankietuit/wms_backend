package com.kit.wmsbackend.feature.refreshtoken.repository;

import com.kit.wmsbackend.entity.RefreshToken;
import com.kit.wmsbackend.repository.BaseAuditRepository;
import com.kit.wmsbackend.specification.BaseSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface RefreshTokenRepository extends BaseAuditRepository<RefreshToken> {
     default Optional<RefreshToken> findNotDeletedByJti(String jti) {
          return findOne(
                  Specification
                          .where(BaseSpecification.<RefreshToken>notDeleted())
                          .and((root, query, cb) ->
                                  cb.equal(root.get("jti"), jti))
          );
     }
}
