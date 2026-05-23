package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.feature.auth.dto.AuthGetMeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DateMapper.class})
public interface AuthMapper {
    @Mapping(source = "avatar", target = "avatar")
    AuthGetMeResponse toAuthGetMeResponse(User user, String avatar);
}
