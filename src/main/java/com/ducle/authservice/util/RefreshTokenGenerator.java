package com.ducle.authservice.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class RefreshTokenGenerator {
    public String generateToken() {
        return UUID.randomUUID().toString();
    }
}
