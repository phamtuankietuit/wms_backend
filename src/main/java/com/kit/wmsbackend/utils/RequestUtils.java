package com.kit.wmsbackend.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class RequestUtils {
    public String getUserAgent(@NonNull HttpServletRequest request) {
        return request.getHeader(HttpHeaders.USER_AGENT);
    }

    public String getIpAddress(@NonNull HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        } else {
            ipAddress = ipAddress.split(",")[0];
        }

        return ipAddress;
    }
}
