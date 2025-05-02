package com.ducle.authservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ducle.authservice.model.dto.BasicUserInfoDTO;
import com.ducle.authservice.service.cache.UserCacheService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserCacheService userCacheService;

    public List<BasicUserInfoDTO> getUsersByIds(List<Long> ids) {
        return userCacheService.getUsersByIds(ids);
    }

    public BasicUserInfoDTO getUserById(Long id) {
        return userCacheService.getBasicUserInfoById(id);
    }

}
