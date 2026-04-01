package com.kit.wmsbackend.feature.refreshtoken.service;

import com.kit.wmsbackend.entity.RefreshToken;
import com.kit.wmsbackend.exception.ResourceNotFoundException;
import com.kit.wmsbackend.feature.refreshtoken.repository.RefreshTokenRepository;
import com.kit.wmsbackend.service.SoftDeleteFilterManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final SoftDeleteFilterManager softDeleteFilterManager;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken findActiveByJti(String jti) {
        return softDeleteFilterManager
                .executeWithActiveFilter(() -> refreshTokenRepository.findByJti(jti))
                .orElseThrow(() -> new ResourceNotFoundException("Refresh Token", "jti", jti));
    }

    @Override
    public RefreshToken findNotDeletedByJti(String jti) {
        return refreshTokenRepository.findByJtiAndDeletedAtIsNull(jti)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh Token", "jti", jti));
    }
}
