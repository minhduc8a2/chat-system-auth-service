package com.ducle.authservice.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ducle.authservice.model.dto.AuthResponse;
import com.ducle.authservice.model.dto.LoginRequest;
import com.ducle.authservice.model.dto.RegisterRequest;
import com.ducle.authservice.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private AuthService authService;

    @Value("${api.auth.url}")
    private String baseUrl;

    private static final String REFRESH_TOKEN = "refresh-token";
    private static final String ACCESS_TOKEN = "access-token";

    @Test
    void login_shouldReturnAuthResponse() throws Exception {
        LoginRequest loginRequest = new LoginRequest("testuser", "testpassword");
        String url = baseUrl + "/login";
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        Mockito.when(authService.login(loginRequest)).thenReturn(new AuthResponse(ACCESS_TOKEN, REFRESH_TOKEN));

        mockMvc.perform(
            post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value(ACCESS_TOKEN))
        .andExpect(jsonPath("$.refreshToken").value(REFRESH_TOKEN));
    }


    @Test
    void register_shouldReturnAuthResponse() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testuser", "testpassword","example@gmail.com");
        String url = baseUrl + "/register";
        String requestBody = objectMapper.writeValueAsString(registerRequest);

        Mockito.when(authService.register(registerRequest)).thenReturn(new AuthResponse(ACCESS_TOKEN, REFRESH_TOKEN));

        mockMvc.perform(
            post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value(ACCESS_TOKEN))
        .andExpect(jsonPath("$.refreshToken").value(REFRESH_TOKEN));
    }

    @Test
    void refresh_shouldReturnAuthResponse() throws Exception {
       
        String url = baseUrl + "/refresh";
        String requestBody = objectMapper.writeValueAsString(Map.of("refreshToken", REFRESH_TOKEN));

        Mockito.when(authService.refresh(REFRESH_TOKEN)).thenReturn(new AuthResponse(ACCESS_TOKEN, REFRESH_TOKEN));

        mockMvc.perform(
            post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value(ACCESS_TOKEN))
        .andExpect(jsonPath("$.refreshToken").value(REFRESH_TOKEN));
    }

}
