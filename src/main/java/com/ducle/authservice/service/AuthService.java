package com.ducle.authservice.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ducle.authservice.exception.AlreadyExistsException;
import com.ducle.authservice.model.domain.CustomUserDetails;
import com.ducle.authservice.model.domain.Role;
import com.ducle.authservice.model.dto.AuthResponse;
import com.ducle.authservice.model.dto.CreateProfileRequest;
import com.ducle.authservice.model.dto.LoginRequest;
import com.ducle.authservice.model.dto.RegisterRequest;
import com.ducle.authservice.model.entity.User;
import com.ducle.authservice.repository.UserRepository;
import com.ducle.authservice.util.JwtUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserServiceClient userServiceClient;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        CustomUserDetails userDetails = (CustomUserDetails) authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        String accessToken = jwtUtils.generateToken(userDetails);
        String refreshToken = refreshTokenService.generateRefreshToken(userDetails);
        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.username())) {
            throw new AlreadyExistsException("Username already exists");
        }
        boolean emailExists = userServiceClient.checkEmailExists(registerRequest.email());
        if (emailExists) {
            throw new AlreadyExistsException("Email already exists");
        }

        var user = new User(registerRequest.username(), passwordEncoder.encode(registerRequest.password()),
                Role.USER);
        userRepository.save(user);
        userServiceClient.createUserProfile(new CreateProfileRequest(registerRequest.email()));

        CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(registerRequest.username());
        String accessToken = jwtUtils.generateToken(userDetails);
        String refreshToken = refreshTokenService.generateRefreshToken(userDetails);

        return new AuthResponse(accessToken, refreshToken);
    }

}
