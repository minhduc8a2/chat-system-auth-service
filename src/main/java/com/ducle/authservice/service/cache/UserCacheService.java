package com.ducle.authservice.service.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ducle.authservice.exception.EntityNotExistsException;
import com.ducle.authservice.mapper.BasicUserInfoMapper;
import com.ducle.authservice.model.dto.BasicUserInfoDTO;
import com.ducle.authservice.model.dto.EmailCheckingRequest;
import com.ducle.authservice.model.entity.User;
import com.ducle.authservice.repository.UserRepository;
import com.ducle.authservice.service.UserServiceClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCacheService {
    private final UserRepository userRepository;
    private final UserServiceClient userServiceClient;
    private final BasicUserInfoMapper basicUserInfoMapper;
    private final CacheManager cacheManager;

    @Cacheable(value = "usersById", key = "#id", unless = "#result == null")
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "usersByUsername", key = "#username", unless = "#result == null")
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Cacheable(value = "usersExistsByUsername", key = "#username")
    public boolean userExistsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Cacheable(value = "emailCheckCache", key = "#request.email", unless = "#result == null")
    public Boolean checkEmailExists(EmailCheckingRequest request) {
        return userServiceClient.checkEmailExists(request);
    }

    @Cacheable(value = "basicUserInfoById", key = "#id", unless = "#result == null")
    public BasicUserInfoDTO getBasicUserInfoById(Long id) {
        return userRepository.findById(id)
                .map(basicUserInfoMapper::userToBasicUserInfo)
                .orElse(null);
    }

    public List<BasicUserInfoDTO> getUsersByIds(List<Long> ids) {
        Cache cache = cacheManager.getCache("basicUserInfoById");

        Map<Long, BasicUserInfoDTO> cachedResults = new HashMap<>();
        List<Long> idsToFetch = new ArrayList<>();

        for (Long id : ids) {
            BasicUserInfoDTO cached = cache.get(id, BasicUserInfoDTO.class);
            if (cached != null) {
                cachedResults.put(id, cached);
            } else {
                idsToFetch.add(id);
            }
        }

        if (!idsToFetch.isEmpty()) {
            List<User> loadedUsers = userRepository.findAllById(idsToFetch);
            for (User user : loadedUsers) {
                BasicUserInfoDTO dto = basicUserInfoMapper.userToBasicUserInfo(user);
                cache.put(user.getId(), dto);
                cachedResults.put(user.getId(), dto);
            }
        }

        List<BasicUserInfoDTO> finalResult = new ArrayList<>();
        for (Long id : ids) {
            BasicUserInfoDTO dto = cachedResults.get(id);
            if (dto == null) {
                throw new EntityNotExistsException("User with ID " + id + " not found");

            } else {
                finalResult.add(dto);
            }
        }

        return finalResult;
    }
}
