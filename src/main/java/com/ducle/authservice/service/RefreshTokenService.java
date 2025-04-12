package com.ducle.authservice.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ducle.authservice.model.domain.CustomUserDetails;
import com.ducle.authservice.model.entity.RefreshToken;
import com.ducle.authservice.model.entity.User;
import com.ducle.authservice.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token.expiration-time}")
    private long refreshTokenExpirationTime;

    
    public String generateRefreshToken(CustomUserDetails userDetails) {
        String token = UUID.randomUUID().toString();

        RefreshToken createdRefreshToken = refreshTokenRepository.save(
                new RefreshToken(token,
                        new User(userDetails),
                        Instant.now().plusMillis(refreshTokenExpirationTime)));
        return createdRefreshToken.getToken();
    }
}
