package com.kit.wmsbackend.feature.auth.model;

import com.kit.wmsbackend.entity.Permission;
import com.kit.wmsbackend.entity.Role;
import com.kit.wmsbackend.entity.User;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public class UserPrincipal implements UserDetails {
    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();

        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                if (role == null || role.getCode() == null || role.getCode().isBlank()) {
                    continue;
                }

                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode().trim().toUpperCase(Locale.ROOT)));

                if (role.getPermissions() == null) {
                    continue;
                }

                for (Permission permission : role.getPermissions()) {
                    if (permission == null || permission.getCode() == null || permission.getCode().isBlank()) {
                        continue;
                    }
                    authorities.add(new SimpleGrantedAuthority(permission.getCode().trim()));
                }
            }
        }

        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

