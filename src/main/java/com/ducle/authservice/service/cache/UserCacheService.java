package com.ducle.authservice.service.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.ducle.authservice.exception.EntityNotExistsException;
import com.ducle.authservice.mapper.BasicUserInfoMapper;
import com.ducle.authservice.mapper.UserCacheDTOMapper;
import com.ducle.authservice.model.domain.Role;
import com.ducle.authservice.model.dto.BasicUserInfoDTO;
import com.ducle.authservice.model.dto.cache.UserCacheDTO;
import com.ducle.authservice.model.entity.User;
import com.ducle.authservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCacheService {
    private final UserRepository userRepository;
    private final BasicUserInfoMapper basicUserInfoMapper;
    private final CacheManager cacheManager;
    private final UserCacheDTOMapper userCacheDTOMapper;

    @Cacheable(value = "userById", key = "#id", unless = "#result == null")
    public UserCacheDTO getUserById(Long id) {
        return userCacheDTOMapper.toUserCacheDTO(userRepository.findById(id).orElse(null));
    }

    @Cacheable(value = "userByUsername", key = "#username", unless = "#result == null")
    public UserCacheDTO getUserByUsername(String username) {
        return userCacheDTOMapper.toUserCacheDTO(userRepository.findByUsername(username).orElse(null));
    }

    @Cacheable(value = "userExistsByUsername", key = "#username")
    public boolean userExistsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Cacheable(value = "basicUserInfoById", key = "#id", unless = "#result == null")
    public BasicUserInfoDTO getBasicUserInfoById(Long id) {
        return userRepository.findById(id)
                .map(basicUserInfoMapper::userToBasicUserInfo)
                .orElse(null);
    }

    @Caching(evict = {
            @CacheEvict(value = "userByUsername", key = "#username"),
            @CacheEvict(value = "userExistsByUsername", key = "#username")
    })
    public void createUser(String username, String password, Role role) {
        userRepository.save(new User(username, password, role));
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
