package com.kit.wmsbackend.feature.user.service;

import com.kit.wmsbackend.entity.Role;
import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.feature.role.repository.RoleRepository;
import com.kit.wmsbackend.feature.user.dto.UserCreateRequest;
import com.kit.wmsbackend.feature.user.dto.UserResponse;
import com.kit.wmsbackend.feature.user.dto.UserUpdateRequest;
import com.kit.wmsbackend.feature.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public UserResponse findById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse create(UserCreateRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        });

        User user = new User();
        applyRequest(user, request.getEmail(), request.getPassword(), request.getName(),
                request.getDateOfBirth(), request.getAvatar(), request.getRoleIds());
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse update(String id, UserUpdateRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        userRepository.findByEmail(request.getEmail()).ifPresent(found -> {
            if (!found.getId().equals(id)) {
                throw new IllegalArgumentException("Email already exists: " + request.getEmail());
            }
        });

        applyRequest(existing, request.getEmail(), request.getPassword(), request.getName(),
                request.getDateOfBirth(), request.getAvatar(), request.getRoleIds());
        return toResponse(userRepository.save(existing));
    }

    @Override
    @Transactional
    public void delete(String id) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        userRepository.delete(existing);
    }

    private void applyRequest(User user,
                              String email,
                              String password,
                              String name,
                              java.time.LocalDate dateOfBirth,
                              String avatar,
                              Set<String> roleIds) {
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setDateOfBirth(dateOfBirth);
        user.setAvatar(avatar);
        user.setRoles(loadRoles(roleIds));
    }

    private Set<Role> loadRoles(Set<String> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new HashSet<>();
        }

        List<Role> roles = roleRepository.findAllById(roleIds);
        if (roles.size() != roleIds.size()) {
            Set<String> foundIds = roles.stream().map(Role::getId).collect(Collectors.toSet());
            String missing = roleIds.stream().filter(id -> !foundIds.contains(id))
                    .collect(Collectors.joining(", "));
            throw new EntityNotFoundException("Role not found with id(s): " + missing);
        }

        return new HashSet<>(roles);
    }

    private UserResponse toResponse(User user) {
        Set<String> roleIds = user.getRoles().stream()
                .map(Role::getId)
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

