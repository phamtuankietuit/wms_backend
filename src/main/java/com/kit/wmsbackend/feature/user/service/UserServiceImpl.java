package com.kit.wmsbackend.feature.user.service;

import com.kit.wmsbackend.assembler.ListResponseAssembler;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.entity.*;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.enums.MediaOwnerType;
import com.kit.wmsbackend.enums.MediaResourceType;
import com.kit.wmsbackend.enums.UserStatus;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.auth.service.JwtService;
import com.kit.wmsbackend.feature.media.dto.MediaAssetResponse;
import com.kit.wmsbackend.feature.media.dto.MediaAssetUploadRequest;
import com.kit.wmsbackend.feature.media.repository.MediaAssetRepository;
import com.kit.wmsbackend.feature.media.service.MediaAssetService;
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
import com.kit.wmsbackend.security.TokenHashingService;
import com.kit.wmsbackend.service.CodeGenerator;
import com.kit.wmsbackend.service.OrderedFetchService;
import com.kit.wmsbackend.service.QueryService;
import com.kit.wmsbackend.utils.SecurityUtils;
import com.kit.wmsbackend.utils.ValidateUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    MediaAssetRepository mediaAssetRepository;

    PasswordEncoder passwordEncoder;

    UserMapper userMapper;
    UserWarehouseMapper userWarehouseMapper;
    ListResponseAssembler listResponseAssembler;

    UserListQueryFieldConfig listQueryFieldConfig;
    CodeGenerator codeGenerator;
    QueryService<User> queryService;
    UserWarehouseService userWarehouseService;
    OrderedFetchService orderedFetchService;
    MediaAssetService mediaAssetService;
    ValidateUtils validateUtils;
    ApplicationEventPublisher eventPublisher;
    JwtService jwtService;
    TokenHashingService tokenHashingService;

    @Override
    public ListResponse<List<UserResponse>> list(@NonNull ListRequest request) {
        Page<User> userPage = queryService
                .list(listQueryFieldConfig, userRepository, request, false, false);

        List<UUID> userIds = userPage.getContent()
                .stream()
                .map(User::getId)
                .toList();

        List<User> usersWithRoles = orderedFetchService.fetchInOrder(
                userIds,
                userRepository::findAllWithRolesByIdIn
        );

        Page<UserResponse> responsePage = new PageImpl<>(
                usersWithRoles.stream()
                        .map(userMapper::toUserResponse)
                        .toList(),
                userPage.getPageable(),
                userPage.getTotalElements()
        );

        return listResponseAssembler.toListResponse(
                responsePage
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

        List<Role> roles = roleRepository.findAllNotDeleted(request.roleIds());
        Set<Role> roleSet = new HashSet<>(roles);
        validateUtils.validateEntitiesExist(roles, request.roleIds(), ErrorCode.ROLE_NOT_FOUND);

        List<Warehouse> warehouses = warehouseRepository.findAllNotDeletedAndActive(request.warehouseIds());
        validateUtils.validateEntitiesExist(warehouses, request.warehouseIds(), ErrorCode.WAREHOUSE_NOT_FOUND);

        String userCode = codeGenerator.generateForUser();

        String placeholderPassword = UUID.randomUUID().toString();

        String resetPasswordToken = jwtService.buildOnboardingResetToken(normalizedEmail);

        User user = new User();
        user.setCode(userCode);
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(placeholderPassword));
        user.setName(request.name().trim());
        user.setDateOfBirth(request.dateOfBirth());
        user.setAvatar(request.avatar());
        user.setStatus(UserStatus.PENDING);
        user.setRoles(roleSet);
        user.setResetToken(tokenHashingService.hashToken(resetPasswordToken));

        User savedUser = userRepository.save(user);

        userWarehouseService.assign(savedUser, warehouses);

        eventPublisher.publishEvent(
                new UserCreatedEvent(
                        savedUser.getEmail(),
                        savedUser.getName(),
                        resetPasswordToken
                )
        );

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

        userRepository.softDelete(user, currentUserId);
    }

    @Override
    @Transactional
    public void bulkDelete(@NonNull Set<UUID> ids) {
        UUID currentUserId = SecurityUtils.getCurrentUserIdOrSystem("bulk delete users");

        if (ids.contains(currentUserId)) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Cannot delete your own user account");
        }

        List<User> existingUsers = userRepository.findAllForSoftDelete(ids);

        validateUtils.validateEntitiesExist(existingUsers, ids, ErrorCode.USER_NOT_FOUND_OR_CANNOT_DELETE_ADMIN_ROLE);

        userRepository.softDeleteAll(existingUsers, currentUserId);
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
    public List<UserResponse> bulkRestore(@NonNull Set<UUID> ids) {
        List<User> existingUsers = userRepository.findAllDeletedForRestore(ids);

        validateUtils.validateEntitiesExist(existingUsers, ids, ErrorCode.USER_NOT_FOUND);

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

        List<User> userWithRoles = orderedFetchService.fetchInOrder(
                userIds,
                userRepository::findAllWithRolesByIdIn
        );

        Page<UserDeletedResponse> responsePage = new PageImpl<>(
                userWithRoles.stream().map(userMapper::toUserDeletedResponse).toList(),
                userPage.getPageable(),
                userPage.getTotalElements()
        );

        return listResponseAssembler.toListResponse(
                responsePage
        );
    }

    @Override
    @Transactional
    public UserResponse updateInfo(UUID id, UserInfoUpdateRequest request) {
        User user = validateAndLoadUser(id);

        userMapper.updateInfo(user, request);

        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateRoles(UUID id, @NonNull Set<UUID> ids) {
        User user = validateAndLoadUser(id);

        Set<Role> newRoles = new HashSet<>(roleRepository.findAllNotDeleted(ids));
        validateUtils.validateEntitiesExist(newRoles, ids, ErrorCode.ROLE_NOT_FOUND);
        user.setRoles(newRoles);
        
        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public List<UserWarehouseResponse> updateWarehouses(UUID id, @NonNull Set<UUID> ids) {
        User user = validateAndLoadUser(id);

        List<Warehouse> warehouses = warehouseRepository.findAllNotDeletedAndActive(ids);
        validateUtils.validateEntitiesExist(warehouses, ids, ErrorCode.WAREHOUSE_NOT_FOUND);

        Set<UUID> foundRequestWarehouseIds = warehouses
                .stream()
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

        List<Warehouse> toAdd = warehouses
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

    @Override
    @Transactional
    public List<UserResponse> activate(Set<UUID> ids) {
        return changeStatus(ids, UserStatus.ACTIVE);
    }

    @Override
    @Transactional
    public List<UserResponse> disabled(Set<UUID> ids) {
        return changeStatus(ids, UserStatus.DISABLED);
    }

    @Override
    @Transactional
    public MediaAssetResponse uploadAvatar(UUID userId, MultipartFile file) {
        String publicId = mediaAssetRepository
                .findActiveByOwner(
                        MediaOwnerType.USER,
                        userId,
                        MediaResourceType.IMAGE)
                .stream()
                .findFirst()
                .map(MediaAsset::getPublicId)
                .orElse(null);

        return mediaAssetService.uploadImage(
                MediaOwnerType.USER,
                userId,
                new MediaAssetUploadRequest(file, publicId, true, true, true)
        );
    }

    private @NonNull @Unmodifiable List<UserResponse> changeStatus(
            Set<UUID> ids, UserStatus newStatus
    ) {
        List<User> users = userRepository.findAllNotDeleted(ids);

        validateUtils.validateEntitiesExist(users, ids, ErrorCode.USER_NOT_FOUND);

        if (newStatus == UserStatus.ACTIVE && users.stream().anyMatch(user -> user.getStatus() != UserStatus.DISABLED)) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Only DISABLED users can be activated");
        } else if (users.stream().anyMatch(user -> user.getStatus() == newStatus)) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "One or more users are already in status: " + newStatus);
        }

        users.forEach(user -> user.setStatus(newStatus));

        return users
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    private @NonNull User validateAndLoadUser(UUID id) {
        return userRepository.findNotDeletedById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, id.toString()));
    }

    private @NonNull String normalizeEmail(@NonNull String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}

