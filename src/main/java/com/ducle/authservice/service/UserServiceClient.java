package com.ducle.authservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.ducle.authservice.model.dto.CreateProfileRequest;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    
    @GetMapping("/api/v1/users/email_exists")
    Boolean checkEmailExists(@RequestParam String email);

    @PostMapping("/api/v1/users")
    void createUserProfile(@RequestBody CreateProfileRequest createProfileRequest);
}
