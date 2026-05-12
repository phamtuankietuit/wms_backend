package com.kit.wmsbackend.entity;

import com.kit.wmsbackend.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends BaseAuditEntity {
    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String resetToken;

    @Column(nullable = false)
    private String name;

    private LocalDate dateOfBirth;

    private String avatar;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status = UserStatus.PENDING;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private List<UserWarehouse> usersWarehouses = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "users_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @EqualsAndHashCode.Exclude
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @OneToMany(mappedBy = "assignedTo", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private List<StockTransaction> stockTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "assignedTo", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private List<StockTransactionHistory> stockTransactionHistories = new ArrayList<>();
}

