package com.viettran.reading_story_web.dto.request;

import jakarta.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvatarFrameRequest {
    Integer price;

    @NotNull(message = "NO_FILE_UPLOADED")
    MultipartFile file;
}
