package com.kit.wmsbackend.service;

import com.kit.wmsbackend.dto.user.UserCreateRequest;
import com.kit.wmsbackend.dto.user.UserResponse;
import com.kit.wmsbackend.dto.user.UserUpdateRequest;

import java.util.List;

public interface UserService {
    List<UserResponse> findAll();

    UserResponse findById(String id);

    UserResponse create(UserCreateRequest request);

    UserResponse update(String id, UserUpdateRequest request);

    void delete(String id);
}
