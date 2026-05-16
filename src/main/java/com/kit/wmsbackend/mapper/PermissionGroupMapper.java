package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.PermissionGroup;
import com.kit.wmsbackend.feature.permissiongroup.dto.PermissionGroupResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PermissionMapper.class})
public interface PermissionGroupMapper {
    PermissionGroupResponse toPermissionGroupResponse(PermissionGroup permissionGroup);
}
