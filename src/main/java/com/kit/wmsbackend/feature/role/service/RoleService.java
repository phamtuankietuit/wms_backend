package com.kit.wmsbackend.feature.role.service;

import com.kit.wmsbackend.feature.role.dto.RoleRequest;
import com.kit.wmsbackend.feature.role.dto.RoleResponse;
import jakarta.validation.Valid;

public interface RoleService {
    RoleResponse create(@Valid RoleRequest roleRequest);
}

