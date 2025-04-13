package com.ducle.authservice.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Lettuce.Cluster.Refresh;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ducle.authservice.exception.AlreadyExistsException;
import com.ducle.authservice.exception.EntityNotExistsException;
import com.ducle.authservice.model.domain.CustomUserDetails;
import com.ducle.authservice.model.domain.Role;
import com.ducle.authservice.model.dto.AuthResponse;
import com.ducle.authservice.model.dto.CreateProfileRequest;
import com.ducle.authservice.model.dto.EmailCheckingRequest;
import com.ducle.authservice.model.dto.LoginRequest;
import com.ducle.authservice.model.dto.RegisterRequest;
import com.ducle.authservice.model.entity.RefreshToken;
import com.ducle.authservice.model.entity.User;
import com.ducle.authservice.repository.RefreshTokenRepository;
import com.ducle.authservice.repository.UserRepository;
import com.ducle.authservice.util.JwtUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Value(value = "${jwt.refresh-token.renew-before-time}")
    private long refreshTokenRenewBeforeTime;

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserServiceClient userServiceClient;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        CustomUserDetails userDetails = (CustomUserDetails) authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()))
                .getPrincipal();
        String accessToken = jwtUtils.generateToken(userDetails);
        String refreshToken = refreshTokenService.generateRefreshToken(userDetails);
        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.username())) {
            throw new AlreadyExistsException("Username already exists");
        }
        boolean emailExists = userServiceClient.checkEmailExists(new EmailCheckingRequest(registerRequest.email()));
        if (emailExists) {
            throw new AlreadyExistsException("Email already exists");
        }

        var user = new User(registerRequest.username(), passwordEncoder.encode(registerRequest.password()),
                Role.ROLE_USER);
        userRepository.save(user);
        userServiceClient.createUserProfile(new CreateProfileRequest(registerRequest.email()));

        CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(registerRequest.username());
        String accessToken = jwtUtils.generateToken(userDetails);
        String refreshToken = refreshTokenService.generateRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse refresh(String stringRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(stringRefreshToken)
                .orElseThrow(() -> new EntityNotExistsException("Refresh token not found"));

        User user = refreshToken.getUser();
        String newAccessToken = jwtUtils.generateToken(user);
        String newRefreshToken = refreshToken.getToken();
        // Check if the refresh token is about to expire and renew it if necessary
        if (refreshToken.getExpiryDate().isBefore(Instant.now().plusMillis(refreshTokenRenewBeforeTime))) {
            newRefreshToken = refreshTokenService.generateRefreshToken(user);
            refreshTokenRepository.delete(refreshToken);

        }
        return new AuthResponse(newAccessToken, newRefreshToken);
    }

}
