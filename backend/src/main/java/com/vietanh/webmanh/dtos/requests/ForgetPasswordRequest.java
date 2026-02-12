package com.vietanh.webmanh.dtos.requests;

import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ForgetPasswordRequest {
    @Email(message = "EMAIL_INVALID")
    String email;
}
