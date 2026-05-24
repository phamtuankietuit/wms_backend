package com.kit.wmsbackend.config.properties;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.security.cookie")
public record CookieProperties(
        @NotNull
        Boolean secure,

        @NotBlank
        @Pattern(regexp = "Strict|Lax|None", message = "Cookie SameSite must be Strict, Lax, or None")
        String sameSite
) {
    public boolean secureEnabled() {
        return Boolean.TRUE.equals(secure);
    }

    @AssertTrue(message = "Cookie SameSite=None requires Secure cookies")
    public boolean isSameSiteCompatibleWithSecure() {
        return !"None".equalsIgnoreCase(sameSite) || secureEnabled();
    }
}
