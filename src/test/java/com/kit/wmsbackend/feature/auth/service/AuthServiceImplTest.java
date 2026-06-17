package com.kit.wmsbackend.feature.auth.service;

import com.kit.wmsbackend.entity.MediaAsset;
import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.enums.MediaOwnerType;
import com.kit.wmsbackend.enums.MediaResourceType;
import com.kit.wmsbackend.enums.UserStatus;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.auth.dto.AuthGetMeResponse;
import com.kit.wmsbackend.feature.auth.dto.AuthLoginRequest;
import com.kit.wmsbackend.feature.auth.dto.AuthLoginResponse;
import com.kit.wmsbackend.feature.auth.model.UserPrincipal;
import com.kit.wmsbackend.feature.media.repository.MediaAssetRepository;
import com.kit.wmsbackend.feature.refreshtoken.dto.RefreshTokenRequest;
import com.kit.wmsbackend.feature.refreshtoken.service.RefreshTokenService;
import com.kit.wmsbackend.feature.user.repository.UserRepository;
import com.kit.wmsbackend.mapper.AuthMapper;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
    private static final String NAME = "Nguyen Van A";
    private static final String AVATAR = "https://example.com/avatar.jpg";

    private final UUID userId = UUID.randomUUID();
    private User user;
    private UserPrincipal userPrincipal;
    private AuthLoginRequest loginRequest;
    private Authentication successfulAuthentication;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtService jwtService;

    @Mock
    UserRepository userRepository;

    @Mock
    RefreshTokenService refreshTokenService;

    @Mock
    MediaAssetRepository mediaAssetRepository;

    @Mock
    AuthMapper authMapper;

    @InjectMocks
    AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        user.setName(NAME);
        user.setEmail(NORMALIZED_EMAIL);

        userPrincipal = new UserPrincipal(
                userId,
                NORMALIZED_EMAIL,
                PASSWORD,
                UserStatus.ACTIVE,
                false,
                null,
                null
        );

        loginRequest = new AuthLoginRequest("  USER@Example.COM  ", PASSWORD);
        successfulAuthentication = new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null,
                userPrincipal.getAuthorities()
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void login_whenCredentialsAreValid_returnsTokensAndStoresRefreshToken() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(successfulAuthentication);
        when(jwtService.buildAccessToken(eq(NORMALIZED_EMAIL), anyString())).thenReturn(ACCESS_TOKEN);
        when(jwtService.buildRefreshToken(eq(NORMALIZED_EMAIL), anyString(), anyString())).thenReturn(REFRESH_TOKEN);
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        AuthLoginResponse response = authService.login(loginRequest);

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
        BadCredentialsException exception = new BadCredentialsException("Bad credentials");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(exception);

        assertThatThrownBy(() -> authService.login(loginRequest)).isSameAs(exception);

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

    @Test
    void getMe_whenAuthenticatedUserExists_returnsUserInformation() {
        authenticateCurrentUser();
        MediaAsset mediaAsset = mediaAsset(AVATAR);
        AuthGetMeResponse expectedResponse = new AuthGetMeResponse(
                NAME,
                NORMALIZED_EMAIL,
                AVATAR,
                new Date()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mediaAssetRepository.findActiveByOwner(MediaOwnerType.USER, userId, MediaResourceType.IMAGE))
                .thenReturn(List.of(mediaAsset));
        when(authMapper.toAuthGetMeResponse(user, AVATAR)).thenReturn(expectedResponse);

        AuthGetMeResponse response = authService.getMe();

        assertThat(response).isSameAs(expectedResponse);
        assertThat(response.name()).isEqualTo(NAME);
        assertThat(response.avatar()).isEqualTo(AVATAR);
        assertThat(response.email()).isEqualTo(NORMALIZED_EMAIL);

        verify(userRepository).findById(userId);
        verify(mediaAssetRepository).findActiveByOwner(MediaOwnerType.USER, userId, MediaResourceType.IMAGE);
        verify(authMapper).toAuthGetMeResponse(user, AVATAR);
    }

    @Test
    void getMe_whenUserHasNoAvatar_returnsUserInformationWithNullAvatar() {
        authenticateCurrentUser();
        AuthGetMeResponse expectedResponse = new AuthGetMeResponse(
                NAME,
                NORMALIZED_EMAIL,
                null,
                new Date()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mediaAssetRepository.findActiveByOwner(MediaOwnerType.USER, userId, MediaResourceType.IMAGE))
                .thenReturn(List.of());
        when(authMapper.toAuthGetMeResponse(user, null)).thenReturn(expectedResponse);

        AuthGetMeResponse response = authService.getMe();

        assertThat(response).isSameAs(expectedResponse);
        assertThat(response.avatar()).isNull();

        verify(userRepository).findById(userId);
        verify(mediaAssetRepository).findActiveByOwner(MediaOwnerType.USER, userId, MediaResourceType.IMAGE);
        verify(authMapper).toAuthGetMeResponse(user, null);
    }

    @Test
    void getMe_whenUserHasMultipleAvatars_usesFirstAvatar() {
        authenticateCurrentUser();
        String secondAvatar = "https://example.com/avatar-2.jpg";
        AuthGetMeResponse expectedResponse = new AuthGetMeResponse(
                NAME,
                NORMALIZED_EMAIL,
                AVATAR,
                new Date()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mediaAssetRepository.findActiveByOwner(MediaOwnerType.USER, userId, MediaResourceType.IMAGE))
                .thenReturn(List.of(mediaAsset(AVATAR), mediaAsset(secondAvatar)));
        when(authMapper.toAuthGetMeResponse(user, AVATAR)).thenReturn(expectedResponse);

        AuthGetMeResponse response = authService.getMe();

        assertThat(response).isSameAs(expectedResponse);
        assertThat(response.avatar()).isEqualTo(AVATAR);

        verify(userRepository).findById(userId);
        verify(mediaAssetRepository).findActiveByOwner(MediaOwnerType.USER, userId, MediaResourceType.IMAGE);
        verify(authMapper).toAuthGetMeResponse(user, AVATAR);
    }

    @Test
    void getMe_whenUserDoesNotExist_throwsUserNotFound() {
        authenticateCurrentUser();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(AppException.class)
                .isThrownBy(() -> authService.getMe())
                .satisfies(exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND));

        verify(userRepository).findById(userId);
        verifyNoInteractions(mediaAssetRepository, authMapper);
    }

    @Test
    void getMe_whenSecurityContextHasNoAuthentication_throwsUnauthorized() {
        assertThatExceptionOfType(AppException.class)
                .isThrownBy(() -> authService.getMe())
                .satisfies(exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_UNAUTHORIZED));

        verifyNoInteractions(userRepository, mediaAssetRepository, authMapper);
    }

    @Test
    void getMe_whenPrincipalIsNotUserPrincipal_throwsUnauthorized() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("anonymousUser", null)
        );

        assertThatExceptionOfType(AppException.class)
                .isThrownBy(() -> authService.getMe())
                .satisfies(exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTH_UNAUTHORIZED));

        verifyNoInteractions(userRepository, mediaAssetRepository, authMapper);
    }

    private void authenticateCurrentUser() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities())
        );
    }

    private @NonNull MediaAsset mediaAsset(String secureUrl) {
        MediaAsset mediaAsset = new MediaAsset();
        mediaAsset.setSecureUrl(secureUrl);
        return mediaAsset;
    }
}
