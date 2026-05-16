package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.Permission;
import com.kit.wmsbackend.feature.permission.dto.PermissionResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionResponse toPermissionResponse(Permission permission);
}
