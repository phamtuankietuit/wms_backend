package com.kit.wmsbackend.feature.auth.service;

import com.kit.wmsbackend.config.properties.JwtProperties;
import com.kit.wmsbackend.entity.RefreshToken;
import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.enums.TokenType;
import com.kit.wmsbackend.feature.refreshtoken.repository.RefreshTokenRepository;
import com.kit.wmsbackend.feature.user.repository.UserRepository;
import com.kit.wmsbackend.security.TokenHashingService;
import com.kit.wmsbackend.utils.RequestUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtService {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final String REFRESH_SESSION_EXPIRES_AT_CLAIM = "refreshSessionExpiresAt";
    private static final String SESSION_ID_CLAIM = "sessionId";

    UserRepository userRepository;
    RefreshTokenRepository refreshTokenRepository;
    TokenHashingService tokenHashingService;
    RequestUtils requestUtils;
    JwtProperties jwtProperties;

    @Transactional
    public void revokeRefreshToken(@NonNull HttpServletRequest request) {
        String jwt = resolveBearerToken(request);

        if (jwt == null) {
            return;
        }

        try {
            revokeParsedToken(jwt);
        } catch (JwtException | IllegalArgumentException ignored) {
            // Invalid logout tokens should not prevent the client from completing logout locally.
        }
    }

    private void revokeParsedToken(@NonNull String jwt) {
        if (isTokenType(jwt, TokenType.REFRESH_TOKEN)) {
            revokeRefreshTokenSession(jwt);
            return;
        }

        if (isTokenType(jwt, TokenType.ACCESS_TOKEN)) {
            revokeBySessionId(extractSessionId(jwt));
        }
    }

    private void revokeRefreshTokenSession(@NonNull String jwt) {
        String sessionId = extractSessionId(jwt);

        if (sessionId != null) {
            revokeBySessionId(sessionId);
            return;
        }

        revokeByJti(extractJti(jwt));
    }

    private void revokeBySessionId(String sessionId) {
        if (sessionId != null) {
            refreshTokenRepository
                    .findNotDeletedBySessionId(sessionId)
                    .ifPresent(refreshTokenRepository::delete);
        }
    }

    private void revokeByJti(String jti) {
        if (jti != null) {
            refreshTokenRepository.findNotDeletedByJti(jti).ifPresent(refreshTokenRepository::delete);
        }
    }

    public String createAccessToken(@NonNull User user, @NonNull String sessionId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SESSION_ID_CLAIM, sessionId);

        return buildToken(claims, user.getEmail(), jwtProperties.expiration(), TokenType.ACCESS_TOKEN);
    }

    @Transactional
    public String createResetToken(@NonNull User user) {
        String token = buildToken(new HashMap<>(), user.getEmail(), jwtProperties.resetExpiration(), TokenType.RESET_TOKEN);
        user.setResetToken(tokenHashingService.hashToken(token));
        userRepository.save(user);

        return token;
    }

    @Transactional
    public String createOnboardingResetToken(@NonNull User user) {
        String token = buildToken(new HashMap<>(), user.getEmail(), jwtProperties.onboardingResetExpiration(), TokenType.RESET_TOKEN);
        user.setResetToken(tokenHashingService.hashToken(token));
        userRepository.save(user);

        return token;
    }

    @Transactional
    public String createRefreshToken(
            @NonNull User user,
            @NonNull String sessionId,
            String jti,
            HttpServletRequest request
    ) {
        Instant sessionExpiresAt = Instant.now().plusMillis(jwtProperties.refreshSessionExpiration());
        return createRefreshToken(user, sessionId, jti, request, sessionExpiresAt);
    }

    @Transactional
    public String createRefreshToken(
            @NonNull User user,
            @NonNull String sessionId,
            String jti,
            HttpServletRequest request,
            @NonNull Instant sessionExpiresAt
    ) {
        Instant tokenExpiresAt = refreshTokenExpiresAt(sessionExpiresAt);
        Map<String, Object> claims = new HashMap<>();
        claims.put(SESSION_ID_CLAIM, sessionId);
        claims.put(REFRESH_SESSION_EXPIRES_AT_CLAIM, sessionExpiresAt.toEpochMilli());

        String token = buildRefreshToken(claims, user.getEmail(), Date.from(tokenExpiresAt), jti);
        createNewRefreshToken(user, sessionId, token, request);

        return token;
    }

    public String resolveBearerToken(@NonNull HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null ||
                !authorizationHeader.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            return null;
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        return token.isEmpty() ? null : token;
    }

    @Transactional
    public void createNewRefreshToken(
            User user,
            @NonNull String sessionId,
            String rawToken,
            HttpServletRequest request
    ) {
        String userAgent = requestUtils.getUserAgent(request);
        String ipAddress = requestUtils.getIpAddress(request);
        String hashedToken = tokenHashingService.hashToken(rawToken);
        Instant now = Instant.now();

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setJti(extractJti(rawToken));
        refreshToken.setSessionId(sessionId);
        refreshToken.setUser(user);
        refreshToken.setToken(hashedToken);
        refreshToken.setUserAgent(userAgent);
        refreshToken.setIpAddress(ipAddress);
        refreshToken.setExpiresAt(extractExpiration(rawToken).toInstant());
        refreshToken.setLastUsedAt(now);

        refreshTokenRepository.save(refreshToken);
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

    public boolean isTokenType(@NonNull String token, @NonNull TokenType tokenType) {
        return tokenType.name().equals(extractClaim(token, claims -> claims.get(TOKEN_TYPE_CLAIM, String.class)));
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

    private String buildToken(
            @NonNull Map<String, Object> extraClaims,
            @NonNull String userEmail,
            long expiration,
            @NonNull TokenType tokenType
    ) {
        extraClaims.put(TOKEN_TYPE_CLAIM, tokenType.name());

        return Jwts.builder()
                .claims(extraClaims)
                .subject(userEmail)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    private String buildRefreshToken(
            @NonNull Map<String, Object> extraClaims,
            @NonNull String userEmail,
            @NonNull Date expiration,
            String jti
    ) {
        extraClaims.put(TOKEN_TYPE_CLAIM, TokenType.REFRESH_TOKEN.name());

        return Jwts.builder()
                .claims(extraClaims)
                .subject(userEmail)
                .id(jti)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expiration)
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

    public boolean isTokenValid(String token, @NonNull UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
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
