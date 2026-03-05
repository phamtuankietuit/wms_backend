package com.kit.wmsbackend.feature.auth.service;

import com.kit.wmsbackend.security.PermissionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("authz")
@Slf4j
public class AuthorizationService {
    private static final String SUPERADMIN_ROLE = "ROLE_SUPER_ADMIN";

    public boolean has(PermissionCode permissionCode) {
        return hasAuthority(SUPERADMIN_ROLE) || hasAuthority(permissionCode.name());
    }

    private boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Authentication {}", authentication.toString());

        if (!authentication.isAuthenticated()) {
            return false;
        }

        return authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority::equals);
    }
}
