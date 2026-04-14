package com.kit.wmsbackend.config.auditing;

import com.kit.wmsbackend.utils.SecurityUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<UUID> {

    @Override
    public @NonNull Optional<UUID> getCurrentAuditor() {
        return Optional.of(SecurityUtils.getCurrentUserIdOrSystem("JPA auditing"));
    }
}
