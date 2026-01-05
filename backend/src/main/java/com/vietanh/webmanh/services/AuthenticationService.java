package com.vietanh.webmanh.services;

import com.nimbusds.jose.JOSEException;
import com.vietanh.webmanh.dtos.requests.*;
import com.vietanh.webmanh.dtos.responses.AuthenticationResponse;
import com.vietanh.webmanh.dtos.responses.IntrospectResponse;
import com.vietanh.webmanh.dtos.responses.UserResponse;

import java.text.ParseException;

public interface AuthenticationService {
    IntrospectResponse introspect(TokenRequest request);

    AuthenticationResponse login(AuthenticationRequest request);

    void logout(TokenRequest request) throws ParseException, JOSEException;

    AuthenticationResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException;

    UserResponse register(RegisterRequest request);

    void changePassword(ChangePasswordRequest request);

    void forgetPassword(ForgetPasswordRequest request);

    void changePasswordWithResetToken(ChangePasswordWithTokenRequest request) throws ParseException;

    void verifyAccount(VerifyAccountRequest request);
}
