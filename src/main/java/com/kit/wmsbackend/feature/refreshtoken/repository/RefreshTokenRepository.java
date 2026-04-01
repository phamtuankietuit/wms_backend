package com.kit.wmsbackend.feature.refreshtoken.repository;

import com.kit.wmsbackend.entity.RefreshToken;
import com.kit.wmsbackend.repository.BaseAuditRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends BaseAuditRepository<RefreshToken> {
     Optional<RefreshToken> findByJti(String jti);
     Optional<RefreshToken> findByJtiAndDeletedAtIsNull(String jti);
}
