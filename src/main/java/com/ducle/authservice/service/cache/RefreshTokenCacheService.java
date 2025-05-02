package com.ducle.authservice.service.cache;


import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ducle.authservice.model.entity.RefreshToken;
import com.ducle.authservice.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenCacheService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Cacheable(value = "refreshTokens", key = "#refreshToken", unless = "#result == null")
    public RefreshToken findByToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken).orElse(null);
    }
}
