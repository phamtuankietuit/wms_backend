package com.kit.wmsbackend.feature.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String email;
    private String name;
    private LocalDate dateOfBirth;
    private String avatar;
    private Set<String> roleIds;
    private Instant createdAt;
    private Instant updatedAt;
}

