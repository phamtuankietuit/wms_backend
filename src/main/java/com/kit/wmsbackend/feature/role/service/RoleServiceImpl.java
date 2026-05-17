package com.kit.wmsbackend.feature.role.service;

import com.kit.wmsbackend.assembler.ListResponseAssembler;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.entity.Permission;
import com.kit.wmsbackend.entity.Role;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.permission.repository.PermissionRepository;
import com.kit.wmsbackend.feature.role.dto.*;
import com.kit.wmsbackend.feature.role.listqueryfieldconfig.RoleListQueryFieldConfig;
import com.kit.wmsbackend.feature.role.repository.RoleRepository;
import com.kit.wmsbackend.mapper.RoleMapper;
import com.kit.wmsbackend.service.QueryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;
    PermissionRepository permissionRepository;
    QueryService<Role> queryService;
    RoleListQueryFieldConfig listQueryFieldConfig;
    ListResponseAssembler listResponseAssembler;

    @Override
    @Transactional
    public RoleResponse create(@NonNull RoleRequest roleRequest) {
        validateCode(roleRequest.code());

        Role role = roleMapper.toEntity(roleRequest);

        return roleMapper.roleToRoleResponse(roleRepository.save(role));
    }

    @Override
    @Transactional
    public RoleDetailResponse update(UUID id, @NonNull RoleUpdateRequest roleUpdateRequest) {
        Role role = roleRepository.findValidById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, id.toString()));

        Set<UUID> requestPermissionIds = roleUpdateRequest.permissionIds();

        List<Permission> requestPermissions = permissionRepository
                .findAllNotDeleted(requestPermissionIds);

        Set<UUID> foundPermissionIds = requestPermissions
                .stream()
                .map(Permission::getId)
                .collect(Collectors.toSet());

        if (requestPermissions.size() != requestPermissionIds.size()) {
            Set<UUID> missingIds = new HashSet<>(requestPermissionIds);
            missingIds.removeAll(foundPermissionIds);
            throw new AppException(ErrorCode.PERMISSION_NOT_FOUND, String.join(", ", missingIds.stream().map(UUID::toString).toList()));
        }

        role.setPermissions(new HashSet<>(requestPermissions));
        role.setName(roleUpdateRequest.name().trim());

        return roleMapper.toRoleDetailResponse(roleRepository.save(role));
    }

    @Override
    public ListResponse<List<RoleListResponse>> list(ListRequest listRequest) {
        return listResponseAssembler.toListResponse(
            queryService
                    .list(
                            listQueryFieldConfig,
                            roleRepository,
                            listRequest,
                            false,
                            true)
                    .map(roleMapper::toRoleListResponse),
                listRequest.sort(),
                listRequest.filters()
        );
    }

    @Override
    public RoleDetailResponse getById(UUID id) {
        return roleMapper.toRoleDetailResponse(
                roleRepository.findDetailById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, id.toString()))
        );
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Role role = roleRepository.findNotDeletedById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, id.toString()));

        if (Boolean.TRUE.equals(role.getIsSystemRole()) || Boolean.TRUE.equals(role.getIsAdminRole())) {
            throw new AppException(ErrorCode.ROLE_CANNOT_DELETE_PROTECTED, id.toString());
        }

        if (roleRepository.existsUserAssignment(id)) {
            throw new AppException(ErrorCode.ROLE_IN_USE, id.toString());
        }

        roleRepository.deleteRolePermissions(id);
        roleRepository.delete(role);
    }

    private void validateCode(String code) {
            if (roleRepository.existsByCode(code)) {
                throw new AppException(ErrorCode.ROLE_CODE_ALREADY_EXISTS, code);
            }
    }
}

