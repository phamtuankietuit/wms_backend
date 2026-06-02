package com.kit.wmsbackend.utils;

import com.kit.wmsbackend.constant.AuditConstant;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.auth.model.UserPrincipal;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityUtils {
    private static final String ANONYMOUS_USER = "anonymousUser";

    public static UserPrincipal getCurrentUser() {
        return getCurrentUser("current operation");
    }

    public static UserPrincipal getCurrentUser(String operation) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new AppException(ErrorCode.AUTH_UNAUTHORIZED, "Authentication is required for " + operation + ": no security context authentication found.");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal userPrincipal)) {
            throw new AppException(ErrorCode.AUTH_UNAUTHORIZED, "Authentication is required for " + operation + ": authenticated principal is not a valid user.");
        }

        return userPrincipal;
    }

    public static UUID getCurrentUserIdOrSystem(String operation) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return AuditConstant.SYSTEM_USER_ID;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof String principalName && ANONYMOUS_USER.equals(principalName)) {
            return AuditConstant.SYSTEM_USER_ID;
        }

        if (!(principal instanceof UserPrincipal userPrincipal)) {
            throw new AppException(
                    ErrorCode.AUTH_UNAUTHORIZED,
                    "Authentication is invalid for " + operation + ": authenticated principal is not a valid user."
            );
        }

        return extractAndValidateUserId(userPrincipal, operation);
    }

    private static @NonNull UUID extractAndValidateUserId(@NonNull UserPrincipal userPrincipal, String operation) {
        UUID userId = userPrincipal.getId();
        if (userId == null) {
            throw new AppException(
                    ErrorCode.AUTH_UNAUTHORIZED,
                    "Authentication is required for " + operation + ": authenticated user id is missing."
            );
        }
        return userId;
    }
}
