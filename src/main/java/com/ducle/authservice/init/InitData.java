package com.ducle.authservice.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ducle.authservice.model.domain.Role;
import com.ducle.authservice.model.entity.User;
import com.ducle.authservice.repository.RefreshTokenRepository;
import com.ducle.authservice.repository.UserRepository;

@Configuration
public class InitData {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            User user = new User("minhduc8a2", passwordEncoder.encode("12345678"), Role.ROLE_USER);
            userRepository.save(user);
            User user2 = new User("minhducadmin", passwordEncoder.encode("12345678"), Role.ROLE_ADMIN);
            userRepository.save(user2);

        };
    }
}
