package com.vietanh.webmanh.dtos.responses;

import java.time.Instant;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vietanh.webmanh.constants.Gender;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    String name;
    String id;
    String email;
    Boolean isVerified;
    String imgSrc;

    Instant createdAt;
    Instant updatedAt;

    Gender gender;

    Set<RoleResponse> roles;
}