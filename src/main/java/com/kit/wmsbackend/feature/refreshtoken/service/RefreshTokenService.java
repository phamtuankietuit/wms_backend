package com.kit.wmsbackend.feature.refreshtoken.service;

import com.kit.wmsbackend.entity.RefreshToken;

public interface RefreshTokenService {
    RefreshToken findActiveByJti(String jti);
}
