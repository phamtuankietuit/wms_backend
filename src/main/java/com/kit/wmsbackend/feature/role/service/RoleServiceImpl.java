package com.kit.wmsbackend.feature.role.service;

import com.kit.wmsbackend.entity.Role;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.role.dto.RoleRequest;
import com.kit.wmsbackend.feature.role.dto.RoleResponse;
import com.kit.wmsbackend.feature.role.repository.RoleRepository;
import com.kit.wmsbackend.mapper.RoleMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;

    @Override
    @Transactional
    public RoleResponse create(@NonNull RoleRequest roleRequest) {
        validateCode(roleRequest.code());

        Role role = roleMapper.toEntity(roleRequest);

        return roleMapper.roleToRoleResponse(roleRepository.save(role));
    }

    private void validateCode(String code) {
            if (roleRepository.existsByCode(code)) {
                throw new AppException(ErrorCode.ROLE_CODE_ALREADY_EXISTS, code);
            }
    }
}

