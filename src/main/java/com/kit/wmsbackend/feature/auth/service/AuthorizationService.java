package com.kit.wmsbackend.feature.auth.service;

import com.kit.wmsbackend.enums.PermissionCode;
import com.kit.wmsbackend.enums.PermissionMatchMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component("authz")
public class AuthorizationService {
    private static final String SUPERADMIN_ROLE = "ROLE_SUPER_ADMIN";

    public boolean hasByMode(PermissionCode permissionCode, String mode) {
        if (permissionCode == null) {
            return false;
        }
        return hasByMode(new PermissionCode[]{permissionCode}, mode);
    }

    public boolean hasByMode(String permissionsTemplate, String mode) {
        return hasByMode(parsePermissions(permissionsTemplate), mode);
    }

    public boolean hasByMode(PermissionCode[] permissions, String mode) {
        PermissionMatchMode matchMode = parseMode(mode);
        return matchMode == PermissionMatchMode.ANY ? hasAny(permissions) : hasAll(permissions);
    }

    public boolean hasAll(PermissionCode... permissions) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isUnauthenticated(authentication)) {
            log.debug("Authentication is null or not authenticated, denying access for ALL permissions check");
            return false;
        }

        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        if (authorities.contains(SUPERADMIN_ROLE)) {
            return true;
        }

        PermissionCode[] normalizedPermissions = normalizePermissions(permissions);
        if (normalizedPermissions.length == 0) {
            log.debug("No permissions provided for ALL mode, denying access by default");
            return false;
        }

        boolean granted = Arrays.stream(normalizedPermissions)
                .map(Enum::name)
                .allMatch(authorities::contains);

        log.debug("ALL permission check principal='{}', permissions={}, granted={}",
                authentication.getName(),
                Arrays.toString(normalizedPermissions),
                granted);

        return granted;
    }

    public boolean hasAny(PermissionCode... permissions) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isUnauthenticated(authentication)) {
            log.debug("Authentication is null or not authenticated, denying access for ANY permissions check");
            return false;
        }

        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        if (authorities.contains(SUPERADMIN_ROLE)) {
            return true;
        }

        PermissionCode[] normalizedPermissions = normalizePermissions(permissions);
        if (normalizedPermissions.length == 0) {
            log.debug("No permissions provided for ANY mode, denying access by default");
            return false;
        }

        boolean granted = Arrays.stream(normalizedPermissions)
                .map(Enum::name)
                .anyMatch(authorities::contains);

        log.debug("ANY permission check principal='{}', permissions={}, granted={}",
                authentication.getName(),
                Arrays.toString(normalizedPermissions),
                granted);

        return granted;
    }

    private boolean isUnauthenticated(Authentication authentication) {
        return authentication == null || !authentication.isAuthenticated();
    }

    private PermissionCode[] normalizePermissions(PermissionCode[] permissions) {
        if (permissions == null || permissions.length == 0) {
            return new PermissionCode[0];
        }

        return Arrays.stream(permissions)
                .filter(Objects::nonNull)
                .toArray(PermissionCode[]::new);
    }

    private PermissionMatchMode parseMode(String mode) {
        if (mode == null || mode.isBlank()) {
            return PermissionMatchMode.ALL;
        }

        try {
            return PermissionMatchMode.valueOf(mode.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid permission match mode '{}', fallback to ALL", mode);
            return PermissionMatchMode.ALL;
        }
    }

    private PermissionCode[] parsePermissions(String permissionsTemplate) {
        if (permissionsTemplate == null || permissionsTemplate.isBlank()) {
            return new PermissionCode[0];
        }

        String normalized = permissionsTemplate
                .replace("[", "")
                .replace("]", "")
                .replace("{", "")
                .replace("}", "")
                .trim();

        if (normalized.isBlank()) {
            return new PermissionCode[0];
        }

        String[] tokens = normalized.split("\\s*,\\s*");
        List<PermissionCode> permissions = new ArrayList<>();

        for (String token : tokens) {
            if (token == null || token.isBlank()) {
                continue;
            }

            try {
                permissions.add(PermissionCode.valueOf(token.trim()));
            } catch (IllegalArgumentException ex) {
                log.warn("Invalid permission token '{}' in annotation template, ignored", token);
            }
        }

        return permissions.toArray(PermissionCode[]::new);
    }
}
