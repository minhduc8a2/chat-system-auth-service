package com.ducle.authservice.model.entity;

import com.ducle.authservice.model.domain.CustomUserDetails;
import com.ducle.authservice.model.domain.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users", indexes = @Index(columnList = "username", name = "idx_username"))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(CustomUserDetails userDetails) {
        this.username = userDetails.getUsername();
        this.password = userDetails.getPassword();
        String authority = userDetails.getAuthorities()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No role found"))
                .getAuthority();

        this.role = Role.valueOf(authority);
    }

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}
