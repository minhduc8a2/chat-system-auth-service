package com.ducle.authservice.mapper;

import org.mapstruct.Mapper;

import com.ducle.authservice.model.dto.BasicUserInfoDTO;
import com.ducle.authservice.model.entity.User;

@Mapper(componentModel = "spring")
public interface BasicUserInfoMapper {

     
     BasicUserInfoDTO userToBasicUserInfo(User user);
}
