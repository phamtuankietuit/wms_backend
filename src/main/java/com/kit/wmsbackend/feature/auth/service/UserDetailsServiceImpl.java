package com.kit.wmsbackend.feature.auth.service;

import com.kit.wmsbackend.feature.auth.model.UserPrincipal;
import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public @NonNull UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getStatus(),
                user.isDeleted(),
                userRepository.findActiveRoleCodesByUserId(user.getId()),
                userRepository.findActivePermissionCodesByUserId(user.getId())
        );
    }
}

