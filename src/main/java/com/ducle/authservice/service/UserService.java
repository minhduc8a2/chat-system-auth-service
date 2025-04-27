package com.ducle.authservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ducle.authservice.exception.EntityNotExistsException;
import com.ducle.authservice.mapper.BasicUserInfoMapper;
import com.ducle.authservice.model.dto.BasicUserInfoDTO;
import com.ducle.authservice.model.entity.User;
import com.ducle.authservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BasicUserInfoMapper basicUserInfoMapper;

    public BasicUserInfoDTO getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotExistsException("User not found."));
        return basicUserInfoMapper.userToBasicUserInfo(user);
    }

    public List<BasicUserInfoDTO> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return users.stream().map(basicUserInfoMapper::userToBasicUserInfo).toList();
    }

}
