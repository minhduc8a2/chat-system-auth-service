package com.ducle.authservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ducle.authservice.model.domain.CustomUserDetails;
import com.ducle.authservice.model.domain.Role;
import com.ducle.authservice.model.entity.RefreshToken;
import com.ducle.authservice.model.entity.User;
import com.ducle.authservice.repository.RefreshTokenRepository;
import com.ducle.authservice.service.RefreshTokenService;
import com.ducle.authservice.util.RefreshTokenGenerator;



@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {
    @InjectMocks
    private RefreshTokenService refreshTokenService;
    
    @Mock
    RefreshTokenRepository refreshTokenRepository;
    @Mock
    RefreshTokenGenerator refreshTokenGenerator;

    @BeforeEach
    void setUp() throws Exception{
        Field field = RefreshTokenService.class.getDeclaredField("refreshTokenExpirationTime") ;
        field.setAccessible(true);
        field.set(refreshTokenService, 15*24*60*60*1000) ; // Set the value to 1000 milliseconds (1 second)
    }

    @Test
    void testGenerateRefreshTokenByUserDetails() {
        when(refreshTokenGenerator.generateToken()).thenReturn("test-refresh-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(new RefreshToken("test-refresh-token", null, null));
        CustomUserDetails userDetails = new CustomUserDetails(new User(1L,"testUser", "testPassword", Role.USER));

        String refreshToken = refreshTokenService.generateRefreshToken(userDetails);

        assertThat(refreshToken).isEqualTo("test-refresh-token");
    }

    @Test
    void testGenerateRefreshTokenByUser() {
        when(refreshTokenGenerator.generateToken()).thenReturn("test-refresh-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(new RefreshToken("test-refresh-token", null, null));
        User user = new User(1L,"testUser", "testPassword", Role.USER);

        String refreshToken = refreshTokenService.generateRefreshToken(user);

        assertThat(refreshToken).isEqualTo("test-refresh-token");
    }
}
