package com.kit.wmsbackend.feature.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String email;
    private String password;
    private String name;
    private LocalDate dateOfBirth;
    private String avatar;
    private Set<UUID> roleIds;
}

