package com.kit.wmsbackend.feature.refreshtoken.service;

import com.kit.wmsbackend.entity.RefreshToken;
import com.kit.wmsbackend.feature.auth.service.JwtService;
import com.kit.wmsbackend.feature.refreshtoken.dto.RefreshTokenRequest;
import com.kit.wmsbackend.feature.refreshtoken.repository.RefreshTokenRepository;
import com.kit.wmsbackend.security.TokenHashingService;
import com.kit.wmsbackend.utils.RequestUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RefreshTokenServiceImpl implements RefreshTokenService {
    RefreshTokenRepository refreshTokenRepository;
    TokenHashingService tokenHashingService;
    JwtService jwtService;

    @Override
    @Transactional
    public void create(@NonNull RefreshTokenRequest request) {
        String userAgent = RequestUtils.getUserAgent();
        String ipAddress = RequestUtils.getIpAddress();
        String hashedToken = tokenHashingService.hashToken(request.rawToken());
        Instant now = Instant.now();

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setJti(request.jti());
        refreshToken.setSessionId(request.sessionId());
        refreshToken.setUser(request.user());
        refreshToken.setToken(hashedToken);
        refreshToken.setUserAgent(userAgent);
        refreshToken.setIpAddress(ipAddress);
        refreshToken.setExpiresAt(jwtService.extractExpiration(request.rawToken()).toInstant());
        refreshToken.setLastUsedAt(now);

        refreshTokenRepository.save(refreshToken);
    }
}
