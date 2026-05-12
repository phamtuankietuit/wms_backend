package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.Role;
import com.kit.wmsbackend.feature.role.dto.RoleResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {DateMapper.class})
public interface RoleMapper {
    RoleResponse roleToRoleResponse(Role role);
}
