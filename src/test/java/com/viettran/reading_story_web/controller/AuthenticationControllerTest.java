package com.viettran.reading_story_web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettran.reading_story_web.dto.request.AuthenticationRequest;
import com.viettran.reading_story_web.dto.response.AuthenticationResponse;
import com.viettran.reading_story_web.service.AuthenticationService;

@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAuthenticate_ReturnsSuccessResponse() throws Exception {
        // dữ liệu request
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        // response từ service
        AuthenticationResponse mockResponse = new AuthenticationResponse();
        mockResponse.setAccessToken("access-token");
        mockResponse.setRefreshToken("refresh-token");
        mockResponse.setAuthenticated(true);

        // Khi gọi service -> trả về mockResponse
        Mockito.when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenReturn(mockResponse);

        // Gửi request POST /auth/login
        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .with(user("test@example.com").roles("USER")) // filter chain
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").doesNotExist()) // hoặc .isEmpty() nếu bạn set ""
                .andExpect(jsonPath("$.result.accessToken").value("access-token"))
                .andExpect(jsonPath("$.result.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.result.authenticated").value(true));
    }
}
