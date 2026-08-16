package com.viettran.reading_story_web.dto.response;

import java.time.Instant;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.viettran.reading_story_web.enums.Gender;

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

    LevelResponse level;

    String frame;
}
