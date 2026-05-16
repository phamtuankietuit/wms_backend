package com.kit.wmsbackend.feature.permissiongroup.service;

import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.feature.permissiongroup.dto.PermissionGroupResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface PermissionGroupService {
    ListResponse<List<PermissionGroupResponse>> list(@Valid ListRequest listRequest);
    PermissionGroupResponse getById(UUID id);
}

