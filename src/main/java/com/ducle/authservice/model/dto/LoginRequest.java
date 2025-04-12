package com.ducle.authservice.model.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Username is required")
        @Length(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,
        
        @NotBlank(message = "Password is required")
        @Length(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
        String password

      
) {
    
}
