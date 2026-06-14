package com.kit.wmsbackend.feature.refreshtoken.service;

import com.kit.wmsbackend.feature.refreshtoken.dto.RefreshTokenRequest;
import jakarta.validation.Valid;

public interface RefreshTokenService {
    void create(@Valid RefreshTokenRequest request);
}
