package com.ducle.authservice.service;

import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.ducle.authservice.exception.AlreadyExistsException;
import com.ducle.authservice.exception.EntityNotExistsException;
import com.ducle.authservice.model.domain.Role;
import com.ducle.authservice.model.dto.AuthResponse;
import com.ducle.authservice.model.dto.EmailCheckingRequest;
import com.ducle.authservice.model.dto.LoginRequest;
import com.ducle.authservice.model.dto.RegisterRequest;
import com.ducle.authservice.model.dto.UserDTO;
import com.ducle.authservice.model.entity.RefreshToken;
import com.ducle.authservice.model.entity.User;
import com.ducle.authservice.repository.RefreshTokenRepository;
import com.ducle.authservice.repository.UserRepository;

import com.ducle.authservice.util.JwtUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void setUp() throws Exception {
        ReflectionTestUtils.setField(authService, "refreshTokenRenewBeforeTime", 259200000L);

    }

    @Test
    void testLoginSuccess() {
        LoginRequest loginRequest = new LoginRequest("username", "password");
        User user = new User(1L, "username", "password", Role.ROLE_USER);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken(user)).thenReturn("accessToken");
        when(refreshTokenService.generateRefreshToken(user)).thenReturn("refreshToken");

        AuthResponse response = authService.login(loginRequest);
        assertThat(response.accessToken()).isEqualTo("accessToken");
        assertThat(response.refreshToken()).isEqualTo("refreshToken");
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest("user", "pass", "user@example.com");

        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userServiceClient.checkEmailExists(new EmailCheckingRequest("user@example.com"))).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encoded-pass");

        when(userRepository.save(any(User.class))).thenReturn(new User("user", "encoded-pass", Role.ROLE_USER));
       
        when(jwtUtils.generateToken(any(User.class))).thenReturn("access-token");
        when(refreshTokenService.generateRefreshToken(any(User.class))).thenReturn("refresh-token");

        AuthResponse response = authService.register(request);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
    }

    @Test
    void testRegister_UsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest("user", "pass", "email@example.com");
        when(userRepository.existsByUsername("user")).thenReturn(true);
        assertThrows(AlreadyExistsException.class, () -> authService.register(request));
    }

    @Test
    void testRefreshToken_RenewalNeeded() {
        User user = new User("user", "pass", Role.ROLE_USER);
        RefreshToken oldToken = new RefreshToken("old-token", user, Instant.now().plusMillis(1000));

        when(refreshTokenRepository.findByToken("old-token")).thenReturn(Optional.of(oldToken));
        when(jwtUtils.generateToken(user)).thenReturn("access-token");
        when(refreshTokenService.generateRefreshToken(user)).thenReturn("new-token");

        AuthResponse response = authService.refresh("old-token");

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("new-token");
    }

    @Test
    void testRefreshToken_NotFound() {
        when(refreshTokenRepository.findByToken("unknown")).thenReturn(Optional.empty());

        assertThrows(EntityNotExistsException.class, () -> authService.refresh("unknown"));
    }

}
