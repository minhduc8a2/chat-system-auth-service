package com.ducle.authservice.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ducle.authservice.model.dto.cache.UserCacheDTO;
import com.ducle.authservice.model.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-time}")
    private long tokenExpirationTime;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(UserCacheDTO user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", List.of(user.role().name()));
        claims.put("userId", user.id());
        return Jwts.builder()
                .subject(user.username())
                .issuedAt(new Date())
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + tokenExpirationTime))
                .signWith(secretKey)
                .compact();
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", List.of(user.getRole().name()));
        claims.put("userId", user.getId());
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + tokenExpirationTime))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        try {
            return extractAllClaims(token).getSubject();

        } catch (Exception e) {
            return null;
        }
    }

    public boolean isTokenValid(String token) {

        try {
            return !extractAllClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractAllClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token).getPayload();
    }

}
