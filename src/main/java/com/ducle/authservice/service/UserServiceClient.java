package com.ducle.authservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ducle.authservice.model.dto.CreateProfileRequest;
import com.ducle.authservice.model.dto.EmailCheckingRequest;

@FeignClient("${user-service.name}")
public interface UserServiceClient {

    @PostMapping("${api.users.email.exists.url}")
    public Boolean checkEmailExists(@RequestBody EmailCheckingRequest request);

    @PostMapping("${api.users.url}")
    public void createUserProfile(@RequestBody CreateProfileRequest createProfileRequest);
}
