package com.vietanh.webmanh.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {
    @Email(message = "EMAIL_INVALID")
    String email;

    @Size(min = 6, max = 16, message = "INVALID_PASSWORD")
    String password;
}