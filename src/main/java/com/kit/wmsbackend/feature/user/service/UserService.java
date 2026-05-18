package com.kit.wmsbackend.feature.user.service;

import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.feature.user.dto.*;
import com.kit.wmsbackend.feature.userwarehouse.dto.UserWarehouseResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserService {
    ListResponse<List<UserResponse>> list(@Valid ListRequest request);
    UserResponse getById(UUID id);
    UserResponse create(@Valid UserCreateRequest request);
    void delete(UUID id);
    void bulkDelete(@Valid @NotEmpty Set<@NotNull UUID> ids);
    UserResponse restore(UUID id);
    List<UserResponse> bulkRestore(@Valid @NotEmpty Set<@NotNull UUID> ids);
    ListResponse<List<UserDeletedResponse>> listDeleted(@Valid ListRequest request);
    UserResponse updateInfo(UUID id, @Valid UserInfoUpdateRequest request);
    UserResponse updateRoles(UUID id, @NotEmpty Set<@NotNull UUID> ids);
    List<UserWarehouseResponse> updateWarehouses(UUID id, @NotEmpty Set<@NotNull UUID> ids);
    List<UserWarehouseResponse> getWarehouses(UUID id);
    List<UserResponse> activate(@NotEmpty Set<@NotNull UUID> ids);
    List<UserResponse> disabled(@NotEmpty Set<@NotNull UUID> ids);
}

