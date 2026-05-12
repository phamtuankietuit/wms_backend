package com.kit.wmsbackend.feature.user.service;

import com.kit.wmsbackend.assembler.ListResponseAssembler;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.role.repository.RoleRepository;
import com.kit.wmsbackend.feature.user.dto.UserResponse;
import com.kit.wmsbackend.feature.user.listqueryfieldconfig.UserListQueryFieldConfig;
import com.kit.wmsbackend.feature.user.repository.UserRepository;
import com.kit.wmsbackend.mapper.UserMapper;
import com.kit.wmsbackend.service.QueryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;
    ListResponseAssembler listResponseAssembler;
    QueryService<User> queryService;
    UserListQueryFieldConfig listQueryFieldConfig;

    @Override
    public ListResponse<List<UserResponse>> list(@NonNull ListRequest request) {
        Page<User> userPage = queryService
                .list(listQueryFieldConfig, userRepository, request, false, false);

        List<UUID> userIds = userPage.getContent()
                .stream()
                .map(User::getId)
                .toList();

        List<User> usersWithRoles =
                userRepository.findAllWithRolesByIdIn(userIds);

        Page<UserResponse> responsePage = new PageImpl<>(
                usersWithRoles.stream()
                        .map(userMapper::toUserResponse)
                        .toList(),
                userPage.getPageable(),
                userPage.getTotalElements()
        );

        return listResponseAssembler.toListResponse(
                responsePage,
                request.sort(),
                request.filters()
        );
    }

    @Override
    public UserResponse getById(UUID id) {
        return userRepository.findByIdWithRoles(id)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, id.toString()));
    }
}

