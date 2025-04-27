package com.ducle.authservice.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ducle.authservice.model.entity.RefreshToken;
import com.ducle.authservice.model.entity.User;
import com.ducle.authservice.repository.RefreshTokenRepository;
import com.ducle.authservice.util.RefreshTokenGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenGenerator refreshTokenGenerator;

    @Value("${jwt.refresh-token.expiration-time}")
    private long refreshTokenExpirationTime;

   
    public String generateRefreshToken(User user) {
        String token = refreshTokenGenerator.generateToken();

        RefreshToken createdRefreshToken = refreshTokenRepository.save(
                new RefreshToken(token,
                        user,
                        Instant.now().plusMillis(refreshTokenExpirationTime)));
        return createdRefreshToken.getToken();
    }

   

  
}
