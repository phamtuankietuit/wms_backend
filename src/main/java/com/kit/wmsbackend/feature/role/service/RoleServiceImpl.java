package com.kit.wmsbackend.feature.role.service;

import com.kit.wmsbackend.entity.Role;
import com.kit.wmsbackend.feature.role.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Role findById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
    }

    @Override
    @Transactional
    public Role create(Role role) {
        roleRepository.findByName(role.getName()).ifPresent(existing -> {
            throw new IllegalArgumentException("Role name already exists: " + role.getName());
        });
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public Role update(UUID id, Role role) {
        Role existing = findById(id);

        roleRepository.findByName(role.getName()).ifPresent(found -> {
            if (!found.getId().equals(id)) {
                throw new IllegalArgumentException("Role name already exists: " + role.getName());
            }
        });

        existing.setName(role.getName());
        existing.setIsAdminRole(role.getIsAdminRole());
        existing.setIsSystemRole(role.getIsSystemRole());
        return roleRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Role existing = findById(id);
        roleRepository.delete(existing);
    }
}

