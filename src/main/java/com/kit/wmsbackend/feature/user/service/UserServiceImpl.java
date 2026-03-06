package com.kit.wmsbackend.feature.user.service;

import com.kit.wmsbackend.entity.Role;
import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.feature.role.repository.RoleRepository;
import com.kit.wmsbackend.feature.user.dto.UserCreateRequest;
import com.kit.wmsbackend.feature.user.dto.UserResponse;
import com.kit.wmsbackend.feature.user.dto.UserUpdateRequest;
import com.kit.wmsbackend.feature.user.repository.UserRepository;
import com.kit.wmsbackend.exception.ResourceAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UserResponse findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse create(UserCreateRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
            throw new ResourceAlreadyExistsException("Email already exists");
        });

        User user = new User();
        applyRequest(user, request);
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse update(UUID id, UserUpdateRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (request.getEmail() != null) {
            userRepository.findByEmail(request.getEmail()).ifPresent(found -> {
                if (!found.getId().equals(id)) {
                    throw new ResourceAlreadyExistsException("Email already exists");
                }
            });
        }

        applyRequest(existing, request);
        return toResponse(userRepository.save(existing));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        userRepository.delete(existing);
    }

    private void applyRequest(User user, UserCreateRequest request) {
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setAvatar(request.getAvatar());
        user.setRoles(loadRoles(request.getRoleIds()));
    }

    private void applyRequest(User user, UserUpdateRequest request) {
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getRoleIds() != null) {
            user.setRoles(loadRoles(request.getRoleIds()));
        }
    }

    private Set<Role> loadRoles(Set<UUID> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new HashSet<>();
        }

        List<Role> roles = roleRepository.findAllById(roleIds);
        if (roles.size() != roleIds.size()) {
            Set<String> foundIds = roles.stream().map(role -> role.getId().toString()).collect(Collectors.toSet());
            String missing = roleIds.stream().filter(id -> !foundIds.contains(id.toString()))
                    .map(UUID::toString)
                    .collect(Collectors.joining(", "));
            throw new EntityNotFoundException("Role not found with id(s): " + missing);
        }

        return new HashSet<>(roles);
    }

    private UserResponse toResponse(User user) {
        Set<String> roleIds = user.getRoles().stream()
                .map(role -> role.getId().toString())
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getDateOfBirth(),
                user.getAvatar(),
                roleIds,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}

