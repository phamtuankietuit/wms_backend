package com.kit.wmsbackend.feature.auth.service;

import com.kit.wmsbackend.config.properties.JwtProperties;
import com.kit.wmsbackend.entity.RefreshToken;
import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.enums.TokenType;
import com.kit.wmsbackend.feature.refreshtoken.repository.RefreshTokenRepository;
import com.kit.wmsbackend.security.TokenHashingService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtService {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final String REFRESH_SESSION_EXPIRES_AT_CLAIM = "refreshSessionExpiresAt";
    private static final String SESSION_ID_CLAIM = "sessionId";

    RefreshTokenRepository refreshTokenRepository;
    TokenHashingService tokenHashingService;
    JwtProperties jwtProperties;

    @Transactional
    public void revokeToken(String bearerToken) {
        String jwt = resolveBearerToken(bearerToken);
        if (jwt == null) return;

        try {
            refreshTokenRepository.deleteBySessionId(extractSessionId(jwt));
            refreshTokenRepository.deleteByJti(extractJti(jwt));
        } catch (JwtException | IllegalArgumentException ignored) {
            // Invalid logout tokens should not prevent the client from completing logout locally.
        }
    }

    public String buildAccessToken(@NonNull String email, @NonNull String sessionId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SESSION_ID_CLAIM, sessionId);

        Instant expiresAt = tokenExpiresAt(jwtProperties.expiration());

        return buildToken(claims, email, expiresAt, null, TokenType.ACCESS_TOKEN);
    }

    public String buildResetToken(String email) {
        Instant expiresAt = tokenExpiresAt(jwtProperties.resetExpiration());

        return buildToken(new HashMap<>(), email, expiresAt, null, TokenType.RESET_TOKEN);
    }

    public String buildOnboardingResetToken(String email) {
        Instant expiresAt = tokenExpiresAt(jwtProperties.onboardingResetExpiration());

        return buildToken(new HashMap<>(), email, expiresAt, null, TokenType.RESET_TOKEN);
    }

    public String buildRefreshToken(
            String email,
            String sessionId,
            String jti
    ) {
        Instant sessionExpiresAt = Instant.now().plusMillis(jwtProperties.refreshSessionExpiration());
        return buildRefreshToken(email, sessionId, jti, sessionExpiresAt);
    }

    public String buildRefreshToken(
            String email,
            String sessionId,
            String jti,
            Instant sessionExpiresAt
    ) {
        Instant tokenExpiresAt = refreshTokenExpiresAt(sessionExpiresAt);
        Map<String, Object> claims = new HashMap<>();
        claims.put(SESSION_ID_CLAIM, sessionId);
        claims.put(REFRESH_SESSION_EXPIRES_AT_CLAIM, sessionExpiresAt.toEpochMilli());

        return buildToken(claims, email, tokenExpiresAt, jti, TokenType.REFRESH_TOKEN);
    }

    public boolean matchesResetStoredToken(
            @NonNull User user,
            @NonNull String rawToken
    ) {
        String encodedToken = user.getResetToken();
        return encodedToken != null && tokenHashingService.verifyToken(rawToken, encodedToken);
    }

    public boolean matchesStoredRefreshToken(
            @NonNull String rawToken,
            @NonNull RefreshToken refreshToken
    ) {
        return tokenHashingService.verifyToken(rawToken, refreshToken.getToken());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractJti(String token) {
        return extractClaim(token, Claims::getId);
    }

    public String extractSessionId(String token) {
        return extractClaim(token, claims -> claims.get(SESSION_ID_CLAIM, String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Instant extractRefreshSessionExpiresAt(@NonNull String token) {
        Object claim = extractClaim(token, claims -> claims.get(REFRESH_SESSION_EXPIRES_AT_CLAIM));

        if (claim instanceof Number numericClaim) {
            return Instant.ofEpochMilli(numericClaim.longValue());
        }

        if (claim instanceof String stringClaim) {
            try {
                return Instant.ofEpochMilli(Long.parseLong(stringClaim));
            } catch (NumberFormatException exception) {
                throw new JwtException("Invalid refresh token", exception);
            }
        }

        throw new JwtException("Invalid refresh token");
    }

    public <T> T extractClaim(String token, @NonNull Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenType(@NonNull String token, @NonNull TokenType tokenType) {
        return tokenType.name().equals(extractClaim(token, claims -> claims.get(TOKEN_TYPE_CLAIM, String.class)));
    }

    public String resolveBearerToken(String bearerToken) {
        if (bearerToken == null ||
                !bearerToken.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            return null;
        }

        String token = bearerToken.substring(BEARER_PREFIX.length()).trim();
        return token.isEmpty() ? null : token;
    }

    private String buildToken(
            @NonNull Map<String, Object> extraClaims,
            String userEmail,
            Instant expiration,
            String jti,
            @NonNull TokenType tokenType
    ) {
        extraClaims.put(TOKEN_TYPE_CLAIM, tokenType.name());

        return Jwts.builder()
                .claims(extraClaims)
                .subject(userEmail)
                .id(jti)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(expiration))
                .signWith(getSignInKey())
                .compact();
    }

    private @NonNull Instant refreshTokenExpiresAt(@NonNull Instant sessionExpiresAt) {
        Instant now = Instant.now();
        Instant idleExpiresAt = now.plusMillis(jwtProperties.refreshExpiration());
        Instant tokenExpiresAt = sessionExpiresAt.isBefore(idleExpiresAt) ? sessionExpiresAt : idleExpiresAt;

        if (!tokenExpiresAt.isAfter(now)) {
            throw new JwtException("Invalid refresh token");
        }

        return tokenExpiresAt;
    }

    private Instant tokenExpiresAt(long millis) {
        return Instant.now().plusMillis(millis);
    }

    public boolean isTokenValid(String token, @NonNull UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }
}
