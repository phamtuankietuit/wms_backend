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
import com.kit.wmsbackend.feature.user.dto.*;
import com.kit.wmsbackend.feature.user.listqueryfieldconfig.UserListQueryFieldConfig;
import com.kit.wmsbackend.feature.user.repository.UserRepository;
import com.kit.wmsbackend.feature.userwarehouse.dto.UserWarehouseResponse;
import com.kit.wmsbackend.feature.userwarehouse.repository.UserWarehouseRepository;
import com.kit.wmsbackend.feature.userwarehouse.service.UserWarehouseService;
import com.kit.wmsbackend.feature.warehouse.repository.WarehouseRepository;
import com.kit.wmsbackend.mapper.UserMapper;
import com.kit.wmsbackend.mapper.UserWarehouseMapper;
import com.kit.wmsbackend.service.CodeGenerator;
import com.kit.wmsbackend.service.QueryService;
import com.kit.wmsbackend.config.properties.JwtProperties;
import com.kit.wmsbackend.config.properties.ClientProperties;
import com.kit.wmsbackend.enums.MailTemplate;
import com.kit.wmsbackend.utils.SecurityUtils;
import jakarta.validation.constraints.NotNull;
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
    UserWarehouseRepository userWarehouseRepository;

    PasswordEncoder passwordEncoder;

    UserMapper userMapper;
    UserWarehouseMapper userWarehouseMapper;
    ListResponseAssembler listResponseAssembler;

    UserListQueryFieldConfig listQueryFieldConfig;
    CodeGenerator codeGenerator;
    QueryService<User> queryService;
    JwtService jwtService;
    MailService mailService;
    UserWarehouseService userWarehouseService;

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
        validateWarehousesExist(request.warehouseIds(), warehouses);

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

        Set<UUID> uniqueIds = validateNoDuplicates(ids);

        UUID currentUserId = SecurityUtils.getCurrentUserIdOrSystem("bulk delete users");
        if (uniqueIds.contains(currentUserId)) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Cannot delete your own user account");
        }

        List<User> existingUsers = userRepository
                .findAllForSoftDelete(uniqueIds)
                .stream()
                .toList();

        if (existingUsers.size() != uniqueIds.size()) {
            Set<UUID> foundIds = existingUsers.stream().map(User::getId).collect(Collectors.toSet());
            Set<UUID> missingIds = new HashSet<>(uniqueIds);
            missingIds.removeAll(foundIds);
            throw new AppException(ErrorCode.USER_NOT_FOUND_OR_CANNOT_DELETE_ADMIN_ROLE, String.join(", ", missingIds.stream().map(UUID::toString).toList()));
        }

        userRepository.softDeleteAll(existingUsers);
    }

    @Override
    @Transactional
    public UserResponse restore(UUID id) {
        User user = userRepository.findDeletedById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, id.toString()));

        return userMapper.toUserResponse(userRepository.restore(user));
    }

    @Override
    @Transactional
    public List<UserResponse> bulkRestore(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "IDs collection cannot be empty");
        }

        Set<UUID> uniqueIds = validateNoDuplicates(ids);

        List<User> existingUsers = userRepository.findAllDeletedForRestore(uniqueIds);

        if (existingUsers.size() != uniqueIds.size()) {
            Set<UUID> foundIds = existingUsers.stream().map(User::getId).collect(Collectors.toSet());
            Set<UUID> missingIds = new HashSet<>(uniqueIds);
            missingIds.removeAll(foundIds);
            throw new AppException(ErrorCode.USER_NOT_FOUND, String.join(", ", missingIds.stream().map(UUID::toString).toList()));
        }

        return userRepository
                .restoreAll(existingUsers)
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Override
    public ListResponse<List<UserDeletedResponse>> listDeleted(ListRequest request) {
        Page<User> userPage = queryService
                .list(
                        listQueryFieldConfig,
                        userRepository,
                        request,
                        true,
                        true
                );

        List<UUID> userIds = userPage.getContent()
                .stream()
                .map(User::getId)
                .toList();

        List<User> userWithRoles = userRepository.findAllWithRolesByIdIn(userIds);

        Page<UserDeletedResponse> responsePage = new PageImpl<>(
                userWithRoles.stream().map(userMapper::toUserDeletedResponse).toList(),
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
    @Transactional
    public UserResponse updateInfo(UUID id, UserInfoUpdateRequest request) {
        User user = validateAndLoadUser(id);

        userMapper.updateInfo(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse updateRoles(UUID id, Collection<UUID> ids) {
        User user = validateAndLoadUser(id);

        if (ids == null || ids.isEmpty()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "IDs collection cannot be empty");
        }

        Set<UUID> uniqueIds = validateNoDuplicates(ids);

        Set<Role> newRoles = new HashSet<>(roleRepository.findAllNotDeleted(uniqueIds));

        if (newRoles.size() != uniqueIds.size()) {
            Set<UUID> foundIds = newRoles.stream().map(Role::getId).collect(Collectors.toSet());
            Set<UUID> missingIds = new HashSet<>(uniqueIds);
            missingIds.removeAll(foundIds);
            throw new AppException(ErrorCode.ROLE_NOT_FOUND, String.join(", ", missingIds.stream().map(UUID::toString).toList()));
        }

        user.setRoles(newRoles);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public List<UserWarehouseResponse> updateWarehouses(UUID id, @NotNull Collection<UUID> ids) {
        User user = validateAndLoadUser(id);

        Set<UUID> uniqueRequestWarehouseIds = validateNoDuplicates(ids);

        Set<Warehouse> foundRequestWarehouses;

        if (uniqueRequestWarehouseIds.isEmpty()) {
            foundRequestWarehouses = Collections.emptySet();
        } else {
            foundRequestWarehouses = new HashSet<>(warehouseRepository.findAllNotDeletedAndActive(uniqueRequestWarehouseIds));
            validateWarehousesExist(uniqueRequestWarehouseIds, foundRequestWarehouses);
        }

        Set<UUID> foundRequestWarehouseIds = foundRequestWarehouses.stream()
            .map(Warehouse::getId)
            .collect(Collectors.toSet());

        List<UserWarehouse> existingUserWarehouses = userWarehouseRepository.findAllByUserId(user.getId());

        List<Warehouse> toDelete = existingUserWarehouses
                .stream()
                .map(UserWarehouse::getWarehouse)
                .filter(warehouse -> !foundRequestWarehouseIds.contains(warehouse.getId()))
                .toList();

        Set<UUID> existingWarehouseIds = existingUserWarehouses
                .stream()
                .map(uw -> uw.getWarehouse().getId())
                .collect(Collectors.toSet());

        List<Warehouse> toAdd = foundRequestWarehouses
                .stream()
                .filter(warehouse -> !existingWarehouseIds.contains(warehouse.getId()))
                .toList();

        userWarehouseService.assign(user, toAdd);
        userWarehouseService.unassign(user, toDelete);

        return userWarehouseRepository
                .findAllByUserId(id)
                .stream()
                .map(userWarehouseMapper::toUserWarehouseResponse)
                .toList();
    }

    @Override
    public List<UserWarehouseResponse> getWarehouses(UUID id) {
        validateAndLoadUser(id);

        return userWarehouseRepository
                .findAllByUserId(id)
                .stream()
                .map(userWarehouseMapper::toUserWarehouseResponse)
                .toList();
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

    private @NonNull User validateAndLoadUser(UUID id) {
        return userRepository.findNotDeletedById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, id.toString()));
    }

    private @NonNull Set<UUID> validateNoDuplicates(Collection<UUID> ids) {
        Set<UUID> uniqueIds = new HashSet<>(ids);
        if (uniqueIds.size() != ids.size()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "IDs collection contains duplicate IDs");
        }
        return uniqueIds;
    }

    private void validateWarehousesExist(Collection<UUID> requestedIds, Set<Warehouse> found) {
        if (found.size() != requestedIds.size()) {
            Set<UUID> foundIds = found.stream().map(Warehouse::getId).collect(Collectors.toSet());
            Set<UUID> missingIds = new HashSet<>(requestedIds);
            missingIds.removeAll(foundIds);
            throw new AppException(ErrorCode.WAREHOUSE_NOT_FOUND,
                String.join(", ", missingIds.stream().map(UUID::toString).toList()));
        }
    }

    private @NonNull String normalizeEmail(@NonNull String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}

