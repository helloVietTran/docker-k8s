package com.viettran.reading_story_web.controller;

import java.text.ParseException;

import org.springframework.web.bind.annotation.*;

import com.nimbusds.jose.JOSEException;
import com.viettran.reading_story_web.dto.request.AuthenticationRequest;
import com.viettran.reading_story_web.dto.request.IntrospectRequest;
import com.viettran.reading_story_web.dto.request.LogoutRequest;
import com.viettran.reading_story_web.dto.request.RefreshRequest;
import com.viettran.reading_story_web.dto.response.ApiResponse;
import com.viettran.reading_story_web.dto.response.AuthenticationResponse;
import com.viettran.reading_story_web.dto.response.IntrospectResponse;
import com.viettran.reading_story_web.service.AuthenticationService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder().result(result).build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> refresh(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }
}
