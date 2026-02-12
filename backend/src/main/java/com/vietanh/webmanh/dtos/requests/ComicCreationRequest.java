package com.vietanh.webmanh.dtos.requests;

import com.vietanh.webmanh.constants.Gender;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComicCreationRequest {
    @NotNull
    String comicName;

    String otherName;

    Gender gender;

    @NotNull
    @Future(message = "PUBLISH_TIMESTAMP_ERROR")
    Instant publishAt;

    @NotNull
    String description;

    @NotNull
    Set<Integer> genreCodes; // danh sách code để tìm genre

    MultipartFile coverImage;
}
