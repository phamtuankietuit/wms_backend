package com.kit.wmsbackend.feature.user.service;

import com.kit.wmsbackend.feature.user.dto.UserCreateRequest;
import com.kit.wmsbackend.feature.user.dto.UserResponse;
import com.kit.wmsbackend.feature.user.dto.UserUpdateRequest;

import java.util.List;

public interface UserService {
    List<UserResponse> findAll();

    UserResponse findById(String id);

    UserResponse create(UserCreateRequest request);

    UserResponse update(String id, UserUpdateRequest request);

    void delete(String id);
}

