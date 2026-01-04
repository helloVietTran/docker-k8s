package com.vietanh.webmanh.dtos.requests;


import com.vietanh.webmanh.constrains.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldMatch(first = "password", second = "confirmPassword", message = "PASSWORDS_DO_NOT_MATCH")
public class RegisterRequest {
    @Email(message = "EMAIL_INVALID")
    String email;

    @NotNull(message = "USERNAME_IS_REQUIRED")
    String username;

    @Size(min = 6, max = 16, message = "INVALID_PASSWORD")
    String password;

    String confirmPassword;
}
