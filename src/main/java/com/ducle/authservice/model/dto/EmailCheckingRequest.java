package com.ducle.authservice.model.dto;

import jakarta.validation.constraints.Email;

public record EmailCheckingRequest(
        @Email(message = "Email is not valid") String email) {

}
