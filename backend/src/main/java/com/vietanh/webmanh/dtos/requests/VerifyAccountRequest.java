package com.vietanh.webmanh.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerifyAccountRequest {
    @NotNull(message = "THIS_PROPERTY_IS_NOT_ALLOW_NULL")
    String verifyToken;
}
