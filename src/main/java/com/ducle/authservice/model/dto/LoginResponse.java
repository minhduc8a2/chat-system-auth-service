package com.ducle.authservice.model.dto;

public record LoginResponse(
                String accessToken,
                String refreshToken) {

}
