package com.kit.wmsbackend.feature.user.service;

import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.feature.user.dto.UserResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface UserService {
    ListResponse<List<UserResponse>> list(@Valid ListRequest request);
}

