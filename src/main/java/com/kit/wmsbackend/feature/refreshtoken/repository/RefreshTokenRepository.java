package com.kit.wmsbackend.feature.refreshtoken.repository;

import com.kit.wmsbackend.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
     Optional<RefreshToken> findByJti(String jti);
}
