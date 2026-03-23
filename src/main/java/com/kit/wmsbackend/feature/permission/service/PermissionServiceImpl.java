package com.kit.wmsbackend.feature.permission.service;

import com.kit.wmsbackend.entity.Permission;
import com.kit.wmsbackend.entity.PermissionGroup;
import com.kit.wmsbackend.exception.ResourceAlreadyExistsException;
import com.kit.wmsbackend.exception.ResourceNotFoundException;
import com.kit.wmsbackend.feature.permission.repository.PermissionRepository;
import com.kit.wmsbackend.feature.permissiongroup.repository.PermissionGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionGroupRepository permissionGroupRepository;

    @Override
    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    @Override
    public Permission findById(UUID id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", id));
    }

    @Override
    @Transactional
    public Permission create(Permission permission) {
        permissionRepository.findByCode(permission.getCode()).ifPresent(existing -> {
            throw new IllegalArgumentException("Permission code already exists: " + permission.getCode());
        });

        PermissionGroup group = extractAndLoadGroup(permission);
        permission.setGroup(group);
        return permissionRepository.save(permission);
    }

    @Override
    @Transactional
    public Permission update(UUID id, Permission permission) {
        Permission existing = findById(id);

        permissionRepository.findByCode(permission.getCode()).ifPresent(found -> {
            if (!found.getId().equals(id)) {
                throw new ResourceAlreadyExistsException("Permission code already exists: " + permission.getCode());
            }
        });

        PermissionGroup group = extractAndLoadGroup(permission);

        existing.setGroup(group);
        existing.setCode(permission.getCode());
        existing.setName(permission.getName());
        return permissionRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Permission existing = findById(id);
        permissionRepository.delete(existing);
    }

    private PermissionGroup extractAndLoadGroup(Permission permission) {
        if (permission.getGroup() == null || permission.getGroup().getId() == null) {
            throw new IllegalArgumentException("Permission group id is required");
        }

        UUID groupId = permission.getGroup().getId();
        return permissionGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("PermissionGroup", "id", groupId));
    }
}

