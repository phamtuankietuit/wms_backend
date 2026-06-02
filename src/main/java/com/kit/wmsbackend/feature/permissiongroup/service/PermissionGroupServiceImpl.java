package com.kit.wmsbackend.feature.permissiongroup.service;

import com.kit.wmsbackend.assembler.ListResponseAssembler;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.entity.PermissionGroup;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.permissiongroup.dto.PermissionGroupResponse;
import com.kit.wmsbackend.feature.permissiongroup.listqueryfieldconfig.PermissionGroupListQueryFieldConfig;
import com.kit.wmsbackend.feature.permissiongroup.repository.PermissionGroupRepository;
import com.kit.wmsbackend.mapper.PermissionGroupMapper;
import com.kit.wmsbackend.service.QueryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionGroupServiceImpl implements PermissionGroupService {
    PermissionGroupRepository permissionGroupRepository;
    QueryService<PermissionGroup> queryService;
    ListResponseAssembler listResponseAssembler;
    PermissionGroupListQueryFieldConfig listQueryFieldConfig;
    PermissionGroupMapper permissionGroupMapper;

    @Override
    public ListResponse<List<PermissionGroupResponse>> list(ListRequest listRequest) {
        Page<PermissionGroup> page = queryService
                .list(listQueryFieldConfig, permissionGroupRepository, listRequest);

        List<UUID> permissionGroupIds = page.getContent()
                .stream()
                .map(PermissionGroup::getId)
                .toList();

        List<PermissionGroup> permissionGroupWithPermissions =
                permissionGroupRepository.findAllWithPermissionsByIdIn(permissionGroupIds);

        Page<PermissionGroupResponse> responsePage = new PageImpl<>(
                permissionGroupWithPermissions.stream().map(permissionGroupMapper::toPermissionGroupResponse).toList(),
                page.getPageable(),
                page.getTotalElements()
        );

        return listResponseAssembler.toListResponse(
                responsePage
        );
    }

    @Override
    public PermissionGroupResponse getById(UUID id) {
        return permissionGroupMapper
                .toPermissionGroupResponse(
                        permissionGroupRepository.findNotDeletedById(id)
                                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_GROUP_NOT_FOUND, id.toString()))
                );
    }
}

