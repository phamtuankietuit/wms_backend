package com.kit.wmsbackend.feature.auth.service;

import com.kit.wmsbackend.entity.RefreshToken;
import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.enums.TokenType;
import com.kit.wmsbackend.feature.refreshtoken.repository.RefreshTokenRepository;
import com.kit.wmsbackend.feature.refreshtoken.service.RefreshTokenService;
import com.kit.wmsbackend.feature.user.repository.UserRepository;
import com.kit.wmsbackend.security.TokenHashingService;
import com.kit.wmsbackend.utils.RequestUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final TokenHashingService tokenHashingService;
    private final RequestUtils requestUtils;

    @Value("${app.security.jwt.secret}")
    private String jwtSecret;

    @Value("${app.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${app.security.jwt.refresh-expiration}")
    private long jwtRefreshExpiration;

    @Value("${app.security.jwt.reset-expiration}")
    private long jwtResetExpiration;

    @Transactional
    public void revokeRefreshToken(@NonNull HttpServletRequest request) {
        String jwt = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(TokenType.REFRESH_TOKEN.toString())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        if  (jwt != null) {
            String jti = extractJti(jwt);
            RefreshToken refreshToken = refreshTokenService.findActiveByJti(jti);

            if (refreshToken != null) {
                refreshToken.setDeletedAt(Instant.now());
                refreshTokenRepository.save(refreshToken);
            }
        }
    }

    public String createAccessToken(@NonNull User user) {
        return buildToken(new HashMap<>(), user.getEmail(), jwtExpiration, null);
    }

    @Transactional
    public String createResetToken(@NonNull User user) {
        String token = buildToken(new HashMap<>(), user.getEmail(), jwtResetExpiration, null);
        user.setResetToken(tokenHashingService.hashToken(token));
        userRepository.save(user);

        return token;
    }

    @Transactional
    public String createRefreshToken(@NonNull User user, String jti, HttpServletRequest request) {
        String token = buildToken(new HashMap<>(), user.getEmail(), jwtRefreshExpiration, jti);
        createNewRefreshToken(user, token, request);

        return token;
    }

    @Transactional
    public void createNewRefreshToken(User user, String rawToken, HttpServletRequest request) {
        String userAgent = requestUtils.getUserAgent(request);
        String ipAddress = requestUtils.getIpAddress(request);
        String hashedToken = tokenHashingService.hashToken(rawToken);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setJti(extractJti(rawToken));
        refreshToken.setUser(user);
        refreshToken.setToken(hashedToken);
        refreshToken.setUserAgent(userAgent);
        refreshToken.setIpAddress(ipAddress);
        refreshToken.setExpiresAt(
                LocalDateTime.now().plus(jwtRefreshExpiration, ChronoUnit.MILLIS)
        );
        refreshToken.setLastUsedAt(LocalDateTime.now());

        refreshTokenRepository.save(refreshToken);
    }

    public boolean matchesStoredToken(
            @NonNull User user,
            @NonNull String rawToken,
            @NonNull TokenType tokenType,
            String jti
    ) {
        String encodedToken = switch (tokenType) {
            case REFRESH_TOKEN -> refreshTokenService.findActiveByJti(jti).getToken();
            case RESET_TOKEN -> user.getResetToken();
            case ACCESS_TOKEN -> null;
        };

        return encodedToken != null && tokenHashingService.verifyToken(rawToken, encodedToken);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractJti(String token) {
        return extractClaim(token, Claims::getId);
    }

    public <T> T extractClaim(String token, @NonNull Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            @NonNull String userEmail,
            long expiration,
            String jti
    ) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userEmail)
                .id(jti)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
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
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}

