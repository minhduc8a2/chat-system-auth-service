package com.ducle.authservice.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ducle.authservice.model.domain.Role;
import com.ducle.authservice.model.entity.User;

import io.jsonwebtoken.MalformedJwtException;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {
    @InjectMocks
    private JwtUtils jwtUtils;

    private final String secret = "X0iDLiUGr5em1s0hWKnZCMdJ+irvhURKSo1faNKpGxY="; // Should match your config
    private final long expiration = 1000 * 60 * 60; // 1 hour

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(jwtUtils, "secret", secret);
        ReflectionTestUtils.setField(jwtUtils, "tokenExpirationTime", expiration);
        jwtUtils.init();
    }

    @Test
    void testGenerateTokenAndValidate() {
        User user = new User("testuser", "password", Role.ROLE_USER);

        String token = jwtUtils.generateToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtUtils.extractUsername(token)).isEqualTo("testuser");
        assertThat(jwtUtils.isTokenExpired(token)).isFalse();
        assertThat(jwtUtils.isTokenValid(token)).isTrue();
    }

    @Test
    void shouldGenerateAndValidateTokenFromUserEntity() {
        User user = new User();
        user.setUsername("userentity");
        user.setRole(Role.ROLE_USER);

        String token = jwtUtils.generateToken(user);

        assertThat(jwtUtils.extractUsername(token)).isEqualTo("userentity");
        assertThat(jwtUtils.isTokenExpired(token)).isFalse();
    }

    @Test
    void shouldThrowExceptionForInvalidToken() {
        String invalidToken = "this.is.not.valid.token";

        assertThrows(MalformedJwtException.class, () -> {
            jwtUtils.extractAllClaims(invalidToken);
        });
    }

    @Test
    void shouldDetectExpiredToken() throws InterruptedException {
        // Set short expiration for testing
        ReflectionTestUtils.setField(jwtUtils, "tokenExpirationTime", 1000L);
        jwtUtils.init();

        User user = new User("testuser", "password", Role.ROLE_USER);

        String token = jwtUtils.generateToken(user);

        // Sleep for 1.5 seconds to let token expire
        Thread.sleep(1500);

        assertThat(jwtUtils.isTokenExpired(token)).isTrue();
        assertThat(jwtUtils.isTokenValid(token)).isFalse();
    }

    @Test
    void shouldExtractClaimsProperly() {
        User user = new User("testuser", "password", Role.ROLE_USER);

        String token = jwtUtils.generateToken(user);

        var claims = jwtUtils.extractAllClaims(token);
        assertThat(claims.getSubject()).isEqualTo("testuser");
        assertThat(claims.get("roles")).isInstanceOf(List.class);
        assertThat(claims.get("roles")).isEqualTo(List.of("ROLE_USER"));
    }

}
