package com.ducle.authservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ducle.authservice.model.dto.cache.RefreshTokenCacheDTO;
import com.ducle.authservice.model.entity.RefreshToken;

@Mapper(componentModel = "spring")
public interface RefreshTokenCacheDTOMapper {
    
    @Mapping(target = "userId", source = "user.id")
    RefreshTokenCacheDTO toRefreshTokenCacheDTO(RefreshToken refreshToken);

  

    
}
