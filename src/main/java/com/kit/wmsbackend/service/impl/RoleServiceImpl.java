package com.kit.wmsbackend.service.impl;

import com.kit.wmsbackend.entity.Role;
import com.kit.wmsbackend.repository.RoleRepository;
import com.kit.wmsbackend.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public Role findById(String id) {
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
    public Role update(String id, Role role) {
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
    public void delete(String id) {
        Role existing = findById(id);
        roleRepository.delete(existing);
    }
}
