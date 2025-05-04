package com.ducle.authservice.model.dto.cache;

import com.ducle.authservice.model.domain.Role;

public record UserCacheDTO(
        Long id,
        String username,
        String password,
        Role role

) {

}
