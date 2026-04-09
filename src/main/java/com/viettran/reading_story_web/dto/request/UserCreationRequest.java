package com.viettran.reading_story_web.dto.request;

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
public class UserCreationRequest {
    @Email(message = "EMAIL_INVALID")
    String email;

    @NotNull(message = "USERNAME_IS_REQUIRED")
    String name;

    @Size(min = 6, max = 16, message = "INVALID_PASSWORD")
    String password;

    String confirmPassword;
}
