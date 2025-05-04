package com.ducle.authservice.model.dto.cache;

public record RefreshTokenCacheDTO(
        Long id,
        String token,
        Long userId,
        String expiryDate) {

}
