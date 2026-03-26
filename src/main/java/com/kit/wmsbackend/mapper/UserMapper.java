package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.feature.user.dto.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(User user);
}
