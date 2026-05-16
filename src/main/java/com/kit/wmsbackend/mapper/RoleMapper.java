package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.Role;
import com.kit.wmsbackend.feature.role.dto.RoleRequest;
import com.kit.wmsbackend.feature.role.dto.RoleResponse;
import com.kit.wmsbackend.feature.role.dto.RoleUpdateResponse;
import com.kit.wmsbackend.feature.user.dto.RoleUResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {DateMapper.class, PermissionMapper.class})
public interface RoleMapper {
    RoleResponse roleToRoleResponse(Role role);
    RoleUResponse toUserRoleResponse(Role role);
    Role toEntity(RoleRequest roleRequest);
    RoleUpdateResponse toRoleUpdateResponse(Role role);
}
