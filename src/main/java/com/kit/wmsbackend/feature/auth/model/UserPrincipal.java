package com.kit.wmsbackend.feature.auth.model;

import com.kit.wmsbackend.enums.UserStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserPrincipal implements UserDetails {
    private static final String ROLE_PREFIX = "ROLE_";

    UUID id;
    String email;
    @Nullable String password;
    UserStatus status;
    boolean deleted;
    transient Collection<GrantedAuthority> authorities;

    public UserPrincipal(
            @NonNull UUID id,
            @NonNull String email,
            @Nullable String password,
            @NonNull UserStatus status,
            boolean deleted,
            Collection<String> roleCodes,
            Collection<String> permissionCodes
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.password = password;
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.deleted = deleted;
        this.authorities = buildAuthorities(roleCodes, permissionCodes);
    }

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public @NonNull String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !deleted && status != UserStatus.DISABLED;
    }

    @Override
    public boolean isEnabled() {
        return !deleted && status == UserStatus.ACTIVE;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    private static @NonNull Collection<GrantedAuthority> buildAuthorities(
            Collection<String> roleCodes,
            Collection<String> permissionCodes
    ) {
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        addRoleAuthorities(authorities, roleCodes);
        addPermissionAuthorities(authorities, permissionCodes);
        return Collections.unmodifiableSet(authorities);
    }

    private static void addRoleAuthorities(Set<GrantedAuthority> authorities, Collection<String> roleCodes) {
        if (roleCodes == null) {
            return;
        }

        for (String roleCode : roleCodes) {
            if (hasText(roleCode)) {
                authorities.add(toRoleAuthority(roleCode));
            }
        }
    }

    private static void addPermissionAuthorities(Set<GrantedAuthority> authorities, Collection<String> permissionCodes) {
        if (permissionCodes == null) {
            return;
        }

        for (String permissionCode : permissionCodes) {
            if (hasText(permissionCode)) {
                authorities.add(toPermissionAuthority(permissionCode));
            }
        }
    }

    private static @NonNull SimpleGrantedAuthority toRoleAuthority(@NonNull String roleCode) {
        return new SimpleGrantedAuthority(ROLE_PREFIX + roleCode.trim().toUpperCase(Locale.ROOT));
    }

    private static @NonNull SimpleGrantedAuthority toPermissionAuthority(@NonNull String permissionCode) {
        return new SimpleGrantedAuthority(permissionCode.trim().toUpperCase(Locale.ROOT));
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
