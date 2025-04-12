package com.ducle.authservice;

import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ducle.authservice.exception.AlreadyExistsException;
import com.ducle.authservice.exception.EntityNotExistsException;
import com.ducle.authservice.model.domain.CustomUserDetails;
import com.ducle.authservice.model.domain.Role;
import com.ducle.authservice.model.dto.AuthResponse;
import com.ducle.authservice.model.dto.LoginRequest;
import com.ducle.authservice.model.dto.RegisterRequest;
import com.ducle.authservice.model.entity.RefreshToken;
import com.ducle.authservice.model.entity.User;
import com.ducle.authservice.repository.RefreshTokenRepository;
import com.ducle.authservice.repository.UserRepository;
import com.ducle.authservice.service.AuthService;
import com.ducle.authservice.service.CustomUserDetailsService;
import com.ducle.authservice.service.RefreshTokenService;
import com.ducle.authservice.service.UserServiceClient;
import com.ducle.authservice.util.JwtUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private CustomUserDetailsService customUserDetailsService;
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
        Field field = AuthService.class.getDeclaredField("refreshTokenRenewBeforeTime");
        field.setAccessible(true);
        field.set(authService, 259200000L); // 3 days in milliseconds
    }

    @Test
    void testLoginSuccess() {
        LoginRequest loginRequest = new LoginRequest("username", "password");
        CustomUserDetails userDetails = new CustomUserDetails(new User(1L, "username", "password", Role.USER));
        Authentication authResult = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authResult);
        when(jwtUtils.generateToken(userDetails)).thenReturn("accessToken");
        when(refreshTokenService.generateRefreshToken(userDetails)).thenReturn("refreshToken");

        AuthResponse response = authService.login(loginRequest);
        assertThat(response.accessToken()).isEqualTo("accessToken");
        assertThat(response.refreshToken()).isEqualTo("refreshToken");
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest("user", "pass", "user@example.com");

        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userServiceClient.checkEmailExists("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encoded-pass");

        when(customUserDetailsService.loadUserByUsername("user"))
                .thenReturn(new CustomUserDetails(new User("user", "encoded-pass", Role.USER)));

        when(jwtUtils.generateToken(any(CustomUserDetails.class))).thenReturn("access-token");
        when(refreshTokenService.generateRefreshToken(any(CustomUserDetails.class))).thenReturn("refresh-token");

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
        User user = new User("user", "pass", Role.USER);
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
