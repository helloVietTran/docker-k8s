package com.vietanh.webmanh.dtos.requests;


import com.vietanh.webmanh.constants.Gender;
import com.vietanh.webmanh.constants.StoryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComicRequest {
    @NotNull
    String comicName;

    String otherName;

    @NotNull
    StoryStatus storyStatus;

    Gender gender;

    @NotNull
    String description;

    @NotNull
    Set<Integer> genreCodes; // danh sách code để tìm genre

    MultipartFile coverImage;
}
