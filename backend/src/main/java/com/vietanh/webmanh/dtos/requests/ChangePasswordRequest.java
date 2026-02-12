package com.vietanh.webmanh.dtos.requests;

import com.vietanh.webmanh.constraints.FieldMatch;
import jakarta.validation.constraints.Size;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldMatch(first = "password", second = "confirmPassword", message = "PASSWORDS_DO_NOT_MATCH")
public class ChangePasswordRequest {
    @Size(min = 6, max = 20, message = "INVALID_PASSWORD")
    String oldPassword;

    @Size(min = 6, max = 20, message = "INVALID_PASSWORD")
    String password;
    String confirmPassword;

    String email;
}