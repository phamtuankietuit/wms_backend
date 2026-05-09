package com.kit.wmsbackend.dto;

import java.util.UUID;

public record Auditor(
        UUID id,
        String name,
        String email
) {
}
