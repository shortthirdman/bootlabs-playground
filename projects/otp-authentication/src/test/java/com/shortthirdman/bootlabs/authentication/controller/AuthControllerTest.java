package com.shortthirdman.bootlabs.authentication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shortthirdman.bootlabs.authentication.common.JwtTokenUtil;
import com.shortthirdman.bootlabs.authentication.dto.UserRegistrationRequest;
import com.shortthirdman.bootlabs.authentication.entity.User;
import com.shortthirdman.bootlabs.authentication.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(AuthController.class)
@SpringBootTest(classes = AuthController.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtTokenUtil jwtTokenUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void registerUser() throws Exception {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email("test@example.com")
                .password("test123")
                .phoneNumber("+1502963182")
                .build();

        User mockUser = new User();
        mockUser.setEmail("test@example.com");

        when(userService.registerUser(any(UserRegistrationRequest.class))).thenReturn(mockUser);

        verify(userService, times(1)).registerUser(any(UserRegistrationRequest.class));
        verify(userService, times(1)).initiateEmailVerification("test@example.com");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.message").value("User has been registered successfully. Verification OTP sent to email."));
    }

    @Test
    void createAuthenticationToken() throws Exception {
    }

    @Test
    void verifyEmail() throws Exception {
    }

    @Test
    void resendEmailOtp() throws Exception {
    }

    @Test
    void verifyPhone() throws Exception {
    }

    @Test
    void initiatePhoneVerification() throws Exception {
    }
}