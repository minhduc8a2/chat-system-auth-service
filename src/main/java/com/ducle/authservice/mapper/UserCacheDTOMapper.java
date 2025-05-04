package com.ducle.authservice.mapper;

import org.mapstruct.Mapper;

import com.ducle.authservice.model.dto.cache.UserCacheDTO;
import com.ducle.authservice.model.entity.User;

@Mapper(componentModel = "spring")
public interface UserCacheDTOMapper {
    UserCacheDTO toUserCacheDTO(User user);
    User toUser(UserCacheDTO userCacheDTO);
}
