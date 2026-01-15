package com.vietanh.webmanh.controllers;

import java.text.ParseException;

import com.vietanh.webmanh.dtos.requests.*;
import com.vietanh.webmanh.dtos.responses.ApiResponse;
import com.vietanh.webmanh.dtos.responses.AuthenticationResponse;
import com.vietanh.webmanh.dtos.responses.IntrospectResponse;
import com.vietanh.webmanh.dtos.responses.UserResponse;
import com.vietanh.webmanh.services.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/register")
    ApiResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse result = authenticationService.register(request);
        return ApiResponse.<UserResponse>builder()
                .result(result)
                .build();
    }

    @PutMapping("/verify-account")
    ApiResponse<Void> verifyAccount(@Valid @RequestBody VerifyAccountRequest request) {
        authenticationService.verifyAccount(request);
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest request) {
        var result = authenticationService.login(request);
        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect( @Valid @RequestBody TokenRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder().result(result).build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> refresh(@Valid @RequestBody RefreshTokenRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@Valid @RequestBody TokenRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }

    // forget -> send mail (token) -> change password with reset token
    @PostMapping("/forget-password")
    ApiResponse<Void> forgetPassword(@Valid @RequestBody ForgetPasswordRequest request){
        authenticationService.forgetPassword(request);
        return ApiResponse.<Void>builder().build();
    }

    @PutMapping("/change-password/reset-token")
    ApiResponse<Void> changePasswordWithResetToken (
            @Valid @RequestBody ChangePasswordWithTokenRequest request
    ) throws ParseException {
        authenticationService.changePasswordWithResetToken(request);
        return ApiResponse.<Void>builder().build();
    }

    @PutMapping("/change-password")
    ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request){
        authenticationService.changePassword(request);
        return ApiResponse.<Void>builder().build();
    }
}
