package com.ducle.authservice.service.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ducle.authservice.mapper.RefreshTokenCacheDTOMapper;
import com.ducle.authservice.model.dto.cache.RefreshTokenCacheDTO;
import com.ducle.authservice.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenCacheService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenCacheDTOMapper refreshTokenCacheDTOMapper;

    @Cacheable(value = "refreshTokens", key = "#refreshToken")
    public RefreshTokenCacheDTO findByToken(String refreshToken) {
        return refreshTokenCacheDTOMapper
                .toRefreshTokenCacheDTO(refreshTokenRepository.findByToken(refreshToken).orElse(null));
    }

    @CacheEvict(value = "refreshTokens", key = "#refreshToken")
    public void delete(RefreshTokenCacheDTO refreshToken) {
        refreshTokenRepository.deleteById(refreshToken.id());
    }

}
