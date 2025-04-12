package com.ducle.authservice.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Username is required")
        String username,
        
        @NotBlank(message = "Password is required")
        String password,

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email
) {
    
}
