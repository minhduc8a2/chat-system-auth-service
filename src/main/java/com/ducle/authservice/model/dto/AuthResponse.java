package com.ducle.authservice.model.dto;

public record AuthResponse(
                String accessToken,
                String refreshToken) {

}
