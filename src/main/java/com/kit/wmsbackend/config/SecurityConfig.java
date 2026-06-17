package com.kit.wmsbackend.config;

import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.auth.service.JwtService;
import com.kit.wmsbackend.security.JwtAuthenticationFilter;
import com.kit.wmsbackend.security.ApiAccessDeniedHandler;
import com.kit.wmsbackend.security.ApiAuthenticationEntryPoint;
import com.kit.wmsbackend.constant.SecurityConstant;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.annotation.AnnotationTemplateExpressionDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfig {
    JwtAuthenticationFilter jwtAuthenticationFilter;
    UserDetailsService userDetailsService;
    ApiAuthenticationEntryPoint authenticationEntryPoint;
    ApiAccessDeniedHandler accessDeniedHandler;
    JwtService jwtService;
    ObjectMapper objectMapper;
    Environment environment;

    @Bean
    public SecurityFilterChain securityFilterChain(@NonNull HttpSecurity http) {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests -> {
                    authorizeRequests
                            .requestMatchers(SecurityConstant.PUBLIC_ENDPOINTS)
                            .permitAll();

                    if (!isProductionProfile()) {
                        authorizeRequests
                                .requestMatchers(SecurityConstant.DOCUMENTATION_ENDPOINTS)
                                .permitAll();
                    }

                    authorizeRequests
                            .anyRequest()
                            .authenticated();
                })
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler((req, res, auth) -> {
                            String bearerToken = req.getHeader(HttpHeaders.AUTHORIZATION);
                            jwtService.revokeToken(bearerToken);
                        })
                        .logoutSuccessHandler((req, res, auth)
                                -> writeLogoutSuccessResponse(res))
                )
                .build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        provider.setPreAuthenticationChecks(userDetails -> {
            // AuthService validates account status after credentials are checked to avoid login enumeration.
        });
        provider.setPostAuthenticationChecks(userDetails -> {
            if (!userDetails.isEnabled()) {
                throw new AppException(ErrorCode.AUTH_INVALID_ACCOUNT);
            }
        });

        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(@NonNull AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }

    @Bean
    public static @NonNull AnnotationTemplateExpressionDefaults annotationTemplateExpressionDefaults() {
        return new AnnotationTemplateExpressionDefaults();
    }

    private boolean isProductionProfile() {
        return environment.acceptsProfiles(Profiles.of("prod"));
    }

    private void writeLogoutSuccessResponse(@NonNull HttpServletResponse response) throws IOException {
        ApiResponse<Void> responseBody = ApiResponse.success("Logout successful", null);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}
