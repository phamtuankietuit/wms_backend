package com.kit.wmsbackend.utils;

import com.kit.wmsbackend.config.properties.CookieProperties;
import com.kit.wmsbackend.config.properties.JwtProperties;
import com.kit.wmsbackend.enums.TokenType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
public class CookieUtils {
    JwtProperties jwtProperties;
    CookieProperties cookieProperties;

    public void clearTokenCookies(@NonNull HttpServletResponse response) {
        ResponseCookie accessTokenCookie = buildTokenCookie(TokenType.ACCESS_TOKEN, null, 0);
        ResponseCookie refreshTokenCookie = buildTokenCookie(TokenType.REFRESH_TOKEN, null, 0);

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }

    public void addAccessTokenCookie(@NonNull HttpServletResponse response, String token) {
        ResponseCookie cookie = buildTokenCookie(TokenType.ACCESS_TOKEN, token, jwtProperties.expiration());

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void addRefreshTokenCookie(@NonNull HttpServletResponse response, String token) {
        ResponseCookie cookie = buildTokenCookie(TokenType.REFRESH_TOKEN, token, jwtProperties.refreshExpiration());

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private @NonNull ResponseCookie buildTokenCookie(@NonNull TokenType type, String token, long duration) {
        return ResponseCookie.from(type.toString(), token)
                .httpOnly(true)
                .secure(cookieProperties.secureEnabled())
                .path("/")
                .maxAge(Duration.ofMillis(duration))
                .sameSite(cookieProperties.sameSite())
                .build();
    }
}
