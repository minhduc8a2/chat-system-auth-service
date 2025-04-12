package com.ducle.authservice.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateProfileRequest(
        @NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email) {

}
