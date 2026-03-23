package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.feature.auth.dto.AuthGetMeResponse;
import com.kit.wmsbackend.feature.auth.dto.AuthRegisterRequest;
import com.kit.wmsbackend.feature.auth.dto.AuthRegisterResponse;
import com.kit.wmsbackend.feature.auth.dto.AuthTokenPayload;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    User toUser(AuthRegisterRequest request);
    AuthRegisterResponse toAuthRegisterResponse(User user, AuthTokenPayload authTokenPayload);
    AuthGetMeResponse toAuthGetMeResponse(User user);
}
