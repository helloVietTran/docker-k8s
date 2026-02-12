package com.vietanh.webmanh.dtos.requests;

import com.vietanh.webmanh.constants.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequest {
    String username;
    Gender gender;

    MultipartFile image;
}
