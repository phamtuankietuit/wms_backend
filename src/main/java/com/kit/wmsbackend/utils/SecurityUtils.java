package com.kit.wmsbackend.utils;

import com.kit.wmsbackend.constant.AuditConstant;
import com.kit.wmsbackend.exception.UnauthorizedException;
import com.kit.wmsbackend.feature.auth.model.UserPrincipal;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

@NoArgsConstructor
public final class SecurityUtils {
    public static UserPrincipal getCurrentUser() {
        return getCurrentUser("current operation");
    }

    public static UserPrincipal getCurrentUser(String operation) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new UnauthorizedException("Authentication is required for " + operation + ": no security context authentication found.");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal userPrincipal)) {
            throw new UnauthorizedException("Authentication is required for " + operation + ": authenticated principal is not a valid user.");
        }

        return userPrincipal;
    }

    public static UUID getCurrentUserIdOrSystem() {
        return getCurrentUserIdOrSystem("current operation");
    }

    public static UUID getCurrentUserIdOrSystem(String operation) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return AuditConstant.SYSTEM_USER_ID;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof String principalName && "anonymousUser".equals(principalName)) {
            return AuditConstant.SYSTEM_USER_ID;
        }

        if (!(principal instanceof UserPrincipal userPrincipal)) {
            throw new UnauthorizedException(
                    "Authentication is invalid for " + operation + ": authenticated principal is not a valid user."
            );
        }

        UUID currentUserId = userPrincipal.getId();
        if (currentUserId == null) {
            throw new UnauthorizedException(
                    "Authentication is required for " + operation + ": authenticated user id is missing."
            );
        }

        return currentUserId;
    }

}
