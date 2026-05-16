package com.kit.wmsbackend.feature.role.service;

import com.kit.wmsbackend.feature.role.dto.RoleRequest;
import com.kit.wmsbackend.feature.role.dto.RoleResponse;
import com.kit.wmsbackend.feature.role.dto.RoleUpdateRequest;
import com.kit.wmsbackend.feature.role.dto.RoleUpdateResponse;
import jakarta.validation.Valid;

import java.util.UUID;

public interface RoleService {
    RoleResponse create(@Valid RoleRequest roleRequest);
    RoleUpdateResponse update(UUID id, @Valid RoleUpdateRequest roleUpdateRequest);
}

