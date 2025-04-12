package com.ducle.authservice.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ducle.authservice.exception.UserAlreadyExistsException;
import com.ducle.authservice.repository.UserRepository;
import com.ducle.authservice.util.JwtUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String login(String username, String password) {
        UserDetails userDetails = (UserDetails)  authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        return jwtUtils.generateToken(userDetails);
    }
    public String register(String username, String password) {
        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(username)) {
            throw new UsernameAlreadyExistsException("User already exists");
        }
        var user = new User(username, passwordEncoder.encode(password));
        userRepository.save(user);
        return jwtUtils.generateToken(customUserDetailsService.loadUserByUsername(username));
    }


}
