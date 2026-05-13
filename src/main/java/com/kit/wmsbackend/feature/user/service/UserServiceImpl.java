package com.kit.wmsbackend.feature.user.service;

import com.kit.wmsbackend.assembler.ListResponseAssembler;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.entity.Role;
import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.entity.UserWarehouse;
import com.kit.wmsbackend.entity.Warehouse;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.enums.UserStatus;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.auth.service.JwtService;
import com.kit.wmsbackend.feature.mail.dto.MailDto;
import com.kit.wmsbackend.feature.mail.service.MailService;
import com.kit.wmsbackend.feature.role.repository.RoleRepository;
import com.kit.wmsbackend.feature.user.dto.UserCreateRequest;
import com.kit.wmsbackend.feature.user.dto.UserResponse;
import com.kit.wmsbackend.feature.user.listqueryfieldconfig.UserListQueryFieldConfig;
import com.kit.wmsbackend.feature.user.repository.UserRepository;
import com.kit.wmsbackend.feature.warehouse.repository.WarehouseRepository;
import com.kit.wmsbackend.mapper.UserMapper;
import com.kit.wmsbackend.service.CodeGenerator;
import com.kit.wmsbackend.service.QueryService;
import com.kit.wmsbackend.config.properties.JwtProperties;
import com.kit.wmsbackend.config.properties.ClientProperties;
import com.kit.wmsbackend.enums.MailTemplate;
import com.kit.wmsbackend.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    WarehouseRepository warehouseRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;
    ListResponseAssembler listResponseAssembler;
    QueryService<User> queryService;
    UserListQueryFieldConfig listQueryFieldConfig;
    CodeGenerator codeGenerator;
    JwtService jwtService;
    MailService mailService;
    ClientProperties clientProperties;
    JwtProperties jwtProperties;

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

    @Override
    @Transactional
    public UserResponse create(@NonNull UserCreateRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new AppException(ErrorCode.USER_EMAIL_ALREADY_EXISTS, normalizedEmail);
        }

        Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.roleIds()));
        if (roles.size() != request.roleIds().size()) {
            Set<UUID> foundIds = roles.stream().map(Role::getId).collect(Collectors.toSet());
            Set<UUID> missingIds = new HashSet<>(request.roleIds());
            missingIds.removeAll(foundIds);
            throw new AppException(ErrorCode.ROLE_NOT_FOUND, missingIds.iterator().next().toString());
        }

        Set<Warehouse> warehouses = new HashSet<>(warehouseRepository.findAllById(request.warehouseIds()));
        if (warehouses.size() != request.warehouseIds().size()) {
            Set<UUID> foundIds = warehouses.stream().map(Warehouse::getId).collect(Collectors.toSet());
            Set<UUID> missingIds = new HashSet<>(request.warehouseIds());
            missingIds.removeAll(foundIds);
            throw new AppException(ErrorCode.WAREHOUSE_NOT_FOUND, missingIds.iterator().next().toString());
        }

        String userCode = codeGenerator.generateForUser();

        String placeholderPassword = UUID.randomUUID().toString();

        User user = new User();
        user.setCode(userCode);
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(placeholderPassword));
        user.setName(request.name());
        user.setDateOfBirth(request.dateOfBirth());
        user.setAvatar(request.avatar());
        user.setStatus(UserStatus.PENDING);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        for (Warehouse warehouse : warehouses) {
            UserWarehouse userWarehouse = new UserWarehouse();
            userWarehouse.setUser(savedUser);
            userWarehouse.setWarehouse(warehouse);
            savedUser.getUsersWarehouses().add(userWarehouse);
        }

        userRepository.save(savedUser);

        sendOnboardingEmail(savedUser, normalizedEmail);

        return userMapper.toUserResponse(savedUser);
    }

    @Override
    @Transactional
    public void delete(@NonNull UUID id) {
        UUID currentUserId = SecurityUtils.getCurrentUserIdOrSystem("delete user");
        if (id.equals(currentUserId)) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Cannot delete your own user account");
        }

        User user = userRepository.findForSoftDelete(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND_OR_CANNOT_DELETE_ADMIN_ROLE, id.toString()));

        userRepository.softDelete(user);
    }

    @Override
    @Transactional
    public void bulkDelete(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "IDs collection cannot be empty");
        }

        UUID currentUserId = SecurityUtils.getCurrentUserIdOrSystem("bulk delete users");
        if (ids.contains(currentUserId)) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Cannot delete your own user account");
        }

        List<User> existingUsers = userRepository
                .findAllForSoftDelete(ids)
                .stream()
                .toList();

        if (existingUsers.size() != ids.size()) {
            Set<UUID> foundIds = existingUsers.stream().map(User::getId).collect(Collectors.toSet());
            Set<UUID> missingIds = new HashSet<>(ids);
            missingIds.removeAll(foundIds);
            throw new AppException(ErrorCode.USER_NOT_FOUND_OR_CANNOT_DELETE_ADMIN_ROLE, String.join(", ", missingIds.stream().map(UUID::toString).toList()));
        }

        userRepository.softDeleteAll(existingUsers);
    }

    private void sendOnboardingEmail(@NonNull User user, @NonNull String email) {
        try {
            String resetToken = jwtService.createOnboardingResetToken(user);

            String resetLink = UriComponentsBuilder.fromUriString(clientProperties.url() + "/reset-password")
                    .queryParam("token", resetToken)
                    .build()
                    .toUriString();

            Map<String, Object> props = new HashMap<>();
            props.put("name", user.getName());
            props.put("resetPasswordLink", resetLink);
            props.put("expirationMinutes", Duration.ofMillis(jwtProperties.onboardingResetExpiration()).toMinutes());

            MailDto dataMail = mailService.createMailDto(
                    email,
                    MailTemplate.RESET_PASSWORD,
                    props
            );

            mailService.sendMail(dataMail);
        } catch (Exception e) {
            log.error("Failed to send onboarding email to {}", email, e);
        }
    }

    private @NonNull String normalizeEmail(@NonNull String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}

