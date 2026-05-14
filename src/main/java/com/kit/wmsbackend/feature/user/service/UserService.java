package com.kit.wmsbackend.feature.user.service;

import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.feature.user.dto.UserCreateRequest;
import com.kit.wmsbackend.feature.user.dto.UserDeletedResponse;
import com.kit.wmsbackend.feature.user.dto.UserInfoUpdateRequest;
import com.kit.wmsbackend.feature.user.dto.UserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface UserService {
    ListResponse<List<UserResponse>> list(@Valid ListRequest request);
    UserResponse getById(UUID id);
    UserResponse create(@Valid UserCreateRequest request);
    void delete(UUID id);
    void bulkDelete(@Valid @NotNull Collection<UUID> ids);
    UserResponse restore(UUID id);
    List<UserResponse> bulkRestore(@Valid @NotNull Collection<UUID> ids);
    ListResponse<List<UserDeletedResponse>> listDeleted(@Valid ListRequest request);
    UserResponse updateInfo(UUID id, @Valid UserInfoUpdateRequest request);
    UserResponse updateRoles(UUID id, @NotEmpty Collection<UUID> ids);
}

