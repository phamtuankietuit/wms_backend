package com.kit.wmsbackend.unit.controller;

import com.kit.wmsbackend.feature.auth.controller.AuthController;
import com.kit.wmsbackend.feature.auth.dto.AuthRefreshTokenResponse;
import com.kit.wmsbackend.feature.auth.service.AuthService;
import com.kit.wmsbackend.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    AuthService authService;

    @Mock
    CookieUtils cookieUtils;

    @Test
    void refreshTokenSetsRotatedRefreshTokenCookie() {
        AuthController controller = new AuthController(authService, cookieUtils);
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        AuthRefreshTokenResponse refreshTokenResponse = new AuthRefreshTokenResponse(
                "new-access-token",
                "new-refresh-token"
        );

        when(authService.refreshToken(request)).thenReturn(refreshTokenResponse);

        controller.refreshToken(request, response);

        verify(cookieUtils).addAccessTokenCookie(response, "new-access-token");
        verify(cookieUtils).addRefreshTokenCookie(response, "new-refresh-token");
    }
}
