package com.kit.wmsbackend.feature.role.service;

import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.feature.role.dto.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface RoleService {
    RoleResponse create(@Valid RoleRequest roleRequest);
    RoleUpdateResponse update(UUID id, @Valid RoleUpdateRequest roleUpdateRequest);
    ListResponse<List<RoleListResponse>> list(@Valid ListRequest listRequest);
}

