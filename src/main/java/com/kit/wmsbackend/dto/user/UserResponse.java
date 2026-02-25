package com.kit.wmsbackend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String email;
    private String name;
    private LocalDate dateOfBirth;
    private String avatar;
    private Set<String> roleIds;
    private Instant createdAt;
    private Instant updatedAt;
}
