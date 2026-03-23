package com.kit.wmsbackend.utils;

import com.kit.wmsbackend.exception.UnauthorizedException;
import com.kit.wmsbackend.feature.auth.model.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new UnauthorizedException("User not authenticated");
        }

        return (UserPrincipal) authentication.getPrincipal();
    }
}
