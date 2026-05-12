package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.dto.Auditor;
import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.feature.stocktransaction.dto.AssignedToResponse;
import com.kit.wmsbackend.feature.user.dto.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {DateMapper.class, RoleMapper.class, WarehouseMapper.class})
public interface UserMapper {
    UserResponse toUserResponse(User user);
    AssignedToResponse toAssignedToResponse(User user);
    Auditor toAuditor(User user);
}
