package com.kit.wmsbackend.utils;

import com.kit.wmsbackend.enums.TokenType;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class CookieUtils {
    @Value("${app.security.jwt.expiration}")
    private long expiration;

    @Value("${app.security.jwt.refresh-expiration}")
    private long refreshExpiration;

    public void clearTokenCookies(@NonNull HttpServletResponse response) {
        ResponseCookie accessTokenCookie = buildTokenCookie(TokenType.ACCESS_TOKEN, null, 0);
        ResponseCookie refreshTokenCookie = buildTokenCookie(TokenType.REFRESH_TOKEN, null, 0);

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }

    public void addAccessTokenCookie(@NonNull HttpServletResponse response, String token) {
        ResponseCookie cookie = buildTokenCookie(TokenType.ACCESS_TOKEN, token, expiration);

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void addRefreshTokenCookie(@NonNull HttpServletResponse response, String token) {
        ResponseCookie cookie = buildTokenCookie(TokenType.REFRESH_TOKEN, token, refreshExpiration);

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private @NonNull ResponseCookie buildTokenCookie(@NonNull TokenType type, String token, long duration) {
        return ResponseCookie.from(type.toString(), token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofMillis(duration))
                .sameSite("Lax")
                .build();
    }
}
