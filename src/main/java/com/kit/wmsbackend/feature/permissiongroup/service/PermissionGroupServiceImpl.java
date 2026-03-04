package com.kit.wmsbackend.feature.permissiongroup.service;

import com.kit.wmsbackend.entity.PermissionGroup;
import com.kit.wmsbackend.feature.permissiongroup.repository.PermissionGroupRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionGroupServiceImpl implements PermissionGroupService {
    private final PermissionGroupRepository permissionGroupRepository;

    @Override
    public List<PermissionGroup> findAll() {
        return permissionGroupRepository.findAll();
    }

    @Override
    public PermissionGroup findById(String id) {
        return permissionGroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission group not found with id: " + id));
    }

    @Override
    @Transactional
    public PermissionGroup create(PermissionGroup group) {
        permissionGroupRepository.findByCode(group.getCode()).ifPresent(existing -> {
            throw new IllegalArgumentException("Permission group code already exists: " + group.getCode());
        });
        return permissionGroupRepository.save(group);
    }

    @Override
    @Transactional
    public PermissionGroup update(String id, PermissionGroup group) {
        PermissionGroup existing = findById(id);

        permissionGroupRepository.findByCode(group.getCode()).ifPresent(found -> {
            if (!found.getId().equals(id)) {
                throw new IllegalArgumentException("Permission group code already exists: " + group.getCode());
            }
        });

        existing.setCode(group.getCode());
        existing.setName(group.getName());
        return permissionGroupRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        PermissionGroup existing = findById(id);
        permissionGroupRepository.delete(existing);
    }
}

