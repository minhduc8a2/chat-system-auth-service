package com.ducle.authservice.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ducle.authservice.model.dto.cache.UserCacheDTO;
import com.ducle.authservice.model.entity.RefreshToken;
import com.ducle.authservice.model.entity.User;
import com.ducle.authservice.repository.RefreshTokenRepository;
import com.ducle.authservice.util.RefreshTokenGenerator;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final EntityManager entityManager;

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

    public String generateRefreshToken(UserCacheDTO userCacheDTO) {
        String token = refreshTokenGenerator.generateToken();
        User user = entityManager.getReference(User.class, userCacheDTO.id());
        RefreshToken createdRefreshToken = refreshTokenRepository.save(
                new RefreshToken(token,
                        user,
                        Instant.now().plusMillis(refreshTokenExpirationTime)));
        return createdRefreshToken.getToken();
    }

}
