package com.ducle.authservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ducle.authservice.model.dto.BasicUserInfoDTO;
import com.ducle.authservice.service.UserService;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/auth/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    BasicUserInfoDTO getUserById(@Min(1) @PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/batch")
    List<BasicUserInfoDTO> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }
}
