package com.kit.wmsbackend.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestUtils {
    private static @Nullable HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    public static String getUserAgent() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) return "unknown";

        return request.getHeader(HttpHeaders.USER_AGENT);
    }

    public static String getIpAddress() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) return "unknown";

        String ipAddress = request.getHeader("X-Forwarded-For");

        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        } else {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        return ipAddress;
    }
}
