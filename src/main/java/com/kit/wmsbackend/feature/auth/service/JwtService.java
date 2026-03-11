package com.kit.wmsbackend.feature.auth.service;

import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.enums.TokenType;
import com.kit.wmsbackend.feature.user.repository.UserRepository;
import com.kit.wmsbackend.security.TokenHashingService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final UserRepository userRepository;
    private final TokenHashingService tokenHashingService;

    @Value("${app.security.jwt.secret}")
    private String jwtSecret;

    @Value("${app.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${app.security.jwt.refresh-expiration}")
    private long jwtRefreshExpiration;

    @Value("${app.security.jwt.reset-expiration}")
    private long jwtResetExpiration;

    public String createToken(@NonNull User user, @NonNull TokenType tokenType) {
        return createToken(new HashMap<>(), user, tokenType);
    }

    public String createToken(
            Map<String, Object> extraClaims,
            @NonNull User user,
            @NonNull TokenType tokenType
    ) {
        long expiration = switch (tokenType) {
            case TokenType.ACCESS_TOKEN -> jwtExpiration;
            case TokenType.REFRESH_TOKEN -> jwtRefreshExpiration;
            case TokenType.RESET_TOKEN -> jwtResetExpiration;
        };

        String token = buildToken(extraClaims, user.getEmail(), expiration);

        if (tokenType == TokenType.REFRESH_TOKEN) {
            user.setRefreshToken(tokenHashingService.hashToken(token));
            userRepository.save(user);
        } else if (tokenType == TokenType.RESET_TOKEN) {
            user.setResetToken(tokenHashingService.hashToken(token));
            userRepository.save(user);
        }

        return token;
    }

    public boolean matchesStoredToken(
            @NonNull User user,
            @NonNull String rawToken,
            @NonNull TokenType tokenType
    ) {
        String encodedToken = switch (tokenType) {
            case TokenType.REFRESH_TOKEN -> user.getRefreshToken();
            case TokenType.RESET_TOKEN -> user.getResetToken();
            case TokenType.ACCESS_TOKEN -> null;
        };

        return encodedToken != null && tokenHashingService.verifyToken(rawToken, encodedToken);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, @NonNull Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private String buildToken(Map<String, Object> extraClaims, @NonNull String userEmail, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userEmail)
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

