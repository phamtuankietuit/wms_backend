package com.kit.wmsbackend.feature.auth.service;

import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.enums.UserStatus;
import com.kit.wmsbackend.feature.auth.dto.AuthLoginRequest;
import com.kit.wmsbackend.feature.auth.dto.AuthLoginResponse;
import com.kit.wmsbackend.feature.auth.model.UserPrincipal;
import com.kit.wmsbackend.feature.refreshtoken.dto.RefreshTokenRequest;
import com.kit.wmsbackend.feature.refreshtoken.service.RefreshTokenService;
import com.kit.wmsbackend.feature.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    private static final String NORMALIZED_EMAIL = "user@example.com";
    private static final String PASSWORD = "password";
    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtService jwtService;

    @Mock
    UserRepository userRepository;

    @Mock
    RefreshTokenService refreshTokenService;

    @InjectMocks
    AuthServiceImpl authService;

    @Test
    void login_whenCredentialsAreValid_returnsTokensAndStoresRefreshToken() {
        AuthLoginRequest request = new AuthLoginRequest("  USER@Example.COM  ", PASSWORD);
        UUID userId = UUID.randomUUID();
        UserPrincipal userPrincipal = new UserPrincipal(
                userId,
                NORMALIZED_EMAIL,
                PASSWORD,
                UserStatus.ACTIVE,
                false,
                null,
                null
        );
        Authentication authentication = mock(Authentication.class);
        User user = new User();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(jwtService.buildAccessToken(eq(NORMALIZED_EMAIL), anyString())).thenReturn(ACCESS_TOKEN);
        when(jwtService.buildRefreshToken(eq(NORMALIZED_EMAIL), anyString(), anyString())).thenReturn(REFRESH_TOKEN);
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        AuthLoginResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(response.refreshToken()).isEqualTo(REFRESH_TOKEN);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authenticationTokenCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authenticationTokenCaptor.capture());
        UsernamePasswordAuthenticationToken authenticationToken = authenticationTokenCaptor.getValue();
        assertThat(authenticationToken.getPrincipal()).isEqualTo(NORMALIZED_EMAIL);
        assertThat(authenticationToken.getCredentials()).isEqualTo(PASSWORD);

        ArgumentCaptor<String> accessSessionIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> refreshSessionIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> jtiCaptor = ArgumentCaptor.forClass(String.class);
        verify(jwtService).buildAccessToken(eq(NORMALIZED_EMAIL), accessSessionIdCaptor.capture());
        verify(jwtService).buildRefreshToken(
                eq(NORMALIZED_EMAIL),
                refreshSessionIdCaptor.capture(),
                jtiCaptor.capture()
        );

        String sessionId = accessSessionIdCaptor.getValue();
        String jti = jtiCaptor.getValue();
        assertThat(refreshSessionIdCaptor.getValue()).isEqualTo(sessionId);
        assertThat(UUID.fromString(sessionId).toString()).isEqualTo(sessionId);
        assertThat(UUID.fromString(jti).toString()).isEqualTo(jti);

        verify(userRepository).getReferenceById(userId);

        ArgumentCaptor<RefreshTokenRequest> refreshTokenRequestCaptor =
                ArgumentCaptor.forClass(RefreshTokenRequest.class);
        verify(refreshTokenService).create(refreshTokenRequestCaptor.capture());
        RefreshTokenRequest refreshTokenRequest = refreshTokenRequestCaptor.getValue();
        assertThat(refreshTokenRequest.user()).isSameAs(user);
        assertThat(refreshTokenRequest.jti()).isEqualTo(jti);
        assertThat(refreshTokenRequest.sessionId()).isEqualTo(sessionId);
        assertThat(refreshTokenRequest.rawToken()).isEqualTo(REFRESH_TOKEN);
    }

    @Test
    void login_whenAuthenticationFails_propagatesExceptionAndDoesNotCreateTokens() {
        AuthLoginRequest request = new AuthLoginRequest("  USER@Example.COM  ", PASSWORD);
        BadCredentialsException exception = new BadCredentialsException("Bad credentials");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(exception);

        assertThatThrownBy(() -> authService.login(request)).isSameAs(exception);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authenticationTokenCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authenticationTokenCaptor.capture());
        UsernamePasswordAuthenticationToken authenticationToken = authenticationTokenCaptor.getValue();
        assertThat(authenticationToken.getPrincipal()).isEqualTo(NORMALIZED_EMAIL);
        assertThat(authenticationToken.getCredentials()).isEqualTo(PASSWORD);

        verify(jwtService, never()).buildAccessToken(anyString(), anyString());
        verify(jwtService, never()).buildRefreshToken(anyString(), anyString(), anyString());
        verifyNoInteractions(userRepository, refreshTokenService);
    }
}
