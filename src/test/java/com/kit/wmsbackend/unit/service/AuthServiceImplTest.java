package com.kit.wmsbackend.unit.service;

import com.kit.wmsbackend.config.properties.ClientProperties;
import com.kit.wmsbackend.config.properties.JwtProperties;
import com.kit.wmsbackend.entity.RefreshToken;
import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.enums.TokenType;
import com.kit.wmsbackend.enums.UserStatus;
import com.kit.wmsbackend.feature.auth.dto.AuthLoginRequest;
import com.kit.wmsbackend.feature.auth.dto.AuthLoginResponse;
import com.kit.wmsbackend.feature.auth.dto.AuthRefreshTokenResponse;
import com.kit.wmsbackend.feature.auth.model.UserPrincipal;
import com.kit.wmsbackend.feature.auth.service.AuthServiceImpl;
import com.kit.wmsbackend.feature.auth.service.JwtService;
import com.kit.wmsbackend.feature.mail.service.MailService;
import com.kit.wmsbackend.feature.media.repository.MediaAssetRepository;
import com.kit.wmsbackend.feature.refreshtoken.repository.RefreshTokenRepository;
import com.kit.wmsbackend.feature.user.repository.UserRepository;
import com.kit.wmsbackend.mapper.AuthMapper;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    private static final String EMAIL = "user@example.com";
    private static final String RAW_REFRESH_TOKEN = "raw-refresh-token";
    private static final String SESSION_ID = "session-id";
    private static final String OLD_JTI = "old-jti";
    private static final String ACCESS_TOKEN = "new-access-token";
    private static final String ROTATED_REFRESH_TOKEN = "new-refresh-token";

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtService jwtService;

    @Mock
    UserRepository userRepository;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Mock
    MediaAssetRepository mediaAssetRepository;

    @Mock
    UserDetailsService userDetailsService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuthMapper authMapper;

    @Mock
    MailService mailService;

    @Mock
    JwtProperties jwtProperties;

    @Mock
    ClientProperties clientProperties;

    AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        authService = new AuthServiceImpl(
                authenticationManager,
                jwtService,
                userRepository,
                refreshTokenRepository,
                mediaAssetRepository,
                userDetailsService,
                passwordEncoder,
                authMapper,
                mailService,
                jwtProperties,
                clientProperties
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void refreshTokenRotatesCurrentRefreshTokenOnly() {
        UUID userId = UUID.randomUUID();
        User user = activeUser(userId);
        RefreshToken currentRefreshToken = refreshToken(user);
        UserDetails userDetails = userDetails(userId);
        MockHttpServletRequest request = refreshRequest();
        Instant refreshSessionExpiresAt = Instant.now().plusSeconds(300);

        when(jwtService.resolveBearerToken(request)).thenReturn(RAW_REFRESH_TOKEN);
        when(jwtService.isTokenType(RAW_REFRESH_TOKEN, TokenType.REFRESH_TOKEN)).thenReturn(true);
        when(jwtService.extractRefreshSessionExpiresAt(RAW_REFRESH_TOKEN)).thenReturn(refreshSessionExpiresAt);
        when(jwtService.extractUsername(RAW_REFRESH_TOKEN)).thenReturn(EMAIL);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(userDetails);
        when(jwtService.extractSessionId(RAW_REFRESH_TOKEN)).thenReturn(SESSION_ID);
        when(jwtService.extractJti(RAW_REFRESH_TOKEN)).thenReturn(OLD_JTI);
        when(refreshTokenRepository.findValidByUserIdAndJtiForUpdate(
                eq(userId),
                eq(OLD_JTI),
                any(Instant.class)
        )).thenReturn(Optional.of(currentRefreshToken));
        when(jwtService.isTokenValid(RAW_REFRESH_TOKEN, userDetails)).thenReturn(true);
        when(jwtService.matchesStoredRefreshToken(RAW_REFRESH_TOKEN, currentRefreshToken)).thenReturn(true);
        when(jwtService.createAccessToken(eq(user), eq(SESSION_ID))).thenReturn(ACCESS_TOKEN);
        when(jwtService.createRefreshToken(eq(user), eq(SESSION_ID), anyString(), eq(request), eq(refreshSessionExpiresAt)))
                .thenReturn(ROTATED_REFRESH_TOKEN);

        AuthRefreshTokenResponse response = authService.refreshToken(request);

        assertEquals(ACCESS_TOKEN, response.accessToken());
        assertEquals(ROTATED_REFRESH_TOKEN, response.refreshToken());
        verify(refreshTokenRepository).delete(currentRefreshToken);

        ArgumentCaptor<String> jtiCaptor = ArgumentCaptor.forClass(String.class);
        verify(jwtService).createRefreshToken(eq(user), eq(SESSION_ID), jtiCaptor.capture(), eq(request), eq(refreshSessionExpiresAt));
        verify(jwtService).createAccessToken(eq(user), eq(SESSION_ID));
        assertNotEquals(OLD_JTI, jtiCaptor.getValue());
        assertTrue(isUuid(jtiCaptor.getValue()));
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void loginCreatesAccessAndRefreshTokensForSameSession() {
        UUID userId = UUID.randomUUID();
        User user = activeUser(userId);
        UserDetails userDetails = userDetails(userId);
        MockHttpServletRequest request = new MockHttpServletRequest();
        AuthLoginRequest loginRequest = new AuthLoginRequest(" USER@EXAMPLE.COM ", "password");
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtService.createAccessToken(eq(user), anyString())).thenReturn(ACCESS_TOKEN);
        when(jwtService.createRefreshToken(eq(user), anyString(), anyString(), eq(request)))
                .thenReturn(ROTATED_REFRESH_TOKEN);

        AuthLoginResponse response = authService.login(loginRequest, request);

        assertEquals(ACCESS_TOKEN, response.accessToken());
        assertEquals(ROTATED_REFRESH_TOKEN, response.refreshToken());

        ArgumentCaptor<String> sessionIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> jtiCaptor = ArgumentCaptor.forClass(String.class);
        verify(jwtService).createAccessToken(eq(user), sessionIdCaptor.capture());
        verify(jwtService).createRefreshToken(eq(user), eq(sessionIdCaptor.getValue()), jtiCaptor.capture(), eq(request));
        assertTrue(isUuid(sessionIdCaptor.getValue()));
        assertTrue(isUuid(jtiCaptor.getValue()));
    }

    @Test
    void refreshTokenRejectsWrongTokenTypeWithoutIssuingNewToken() {
        MockHttpServletRequest request = refreshRequest();

        when(jwtService.resolveBearerToken(request)).thenReturn(RAW_REFRESH_TOKEN);
        when(jwtService.isTokenType(RAW_REFRESH_TOKEN, TokenType.REFRESH_TOKEN)).thenReturn(false);

        assertThrows(JwtException.class, () -> authService.refreshToken(request));

        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
        verify(jwtService, never()).createAccessToken(any(), anyString());
        verify(jwtService, never()).createRefreshToken(any(), anyString(), anyString(), any(), any());
    }

    @Test
    void refreshTokenRejectsRevokedTokenWithoutIssuingNewToken() {
        UUID userId = UUID.randomUUID();
        User user = activeUser(userId);
        UserDetails userDetails = userDetails(userId);
        MockHttpServletRequest request = refreshRequest();
        Instant refreshSessionExpiresAt = Instant.now().plusSeconds(300);

        when(jwtService.resolveBearerToken(request)).thenReturn(RAW_REFRESH_TOKEN);
        when(jwtService.isTokenType(RAW_REFRESH_TOKEN, TokenType.REFRESH_TOKEN)).thenReturn(true);
        when(jwtService.extractRefreshSessionExpiresAt(RAW_REFRESH_TOKEN)).thenReturn(refreshSessionExpiresAt);
        when(jwtService.extractUsername(RAW_REFRESH_TOKEN)).thenReturn(EMAIL);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(userDetails);
        when(jwtService.extractJti(RAW_REFRESH_TOKEN)).thenReturn(OLD_JTI);
        when(refreshTokenRepository.findValidByUserIdAndJtiForUpdate(
                eq(userId),
                eq(OLD_JTI),
                any(Instant.class)
        )).thenReturn(Optional.empty());

        assertThrows(JwtException.class, () -> authService.refreshToken(request));

        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
        verify(jwtService, never()).createAccessToken(any(), anyString());
        verify(jwtService, never()).createRefreshToken(any(), anyString(), anyString(), any(), any());
    }

    private static User activeUser(UUID id) {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", id);
        user.setEmail(EMAIL);
        user.setPassword("encoded-password");
        user.setName("Test User");
        user.setCode("USR001");
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    private static UserDetails userDetails(UUID userId) {
        return new UserPrincipal(
                userId,
                EMAIL,
                "encoded-password",
                UserStatus.ACTIVE,
                false,
                List.of(),
                List.of()
        );
    }

    private static RefreshToken refreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setJti(OLD_JTI);
        refreshToken.setToken("stored-token-hash");
        refreshToken.setExpiresAt(Instant.now().plusSeconds(300));
        return refreshToken;
    }

    private static MockHttpServletRequest refreshRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + RAW_REFRESH_TOKEN);
        return request;
    }

    private static boolean isUuid(String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}
