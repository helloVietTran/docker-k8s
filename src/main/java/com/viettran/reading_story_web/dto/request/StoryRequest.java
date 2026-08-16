package com.viettran.reading_story_web.dto.request;

import java.util.Set;

import jakarta.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import com.viettran.reading_story_web.enums.Gender;
import com.viettran.reading_story_web.enums.StoryStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoryRequest {
    @NotNull(message = "SOME_FIELDS_REQUIRED")
    String name;

    String otherName;
    String authorName;

    StoryStatus status;
    String description;

    @NotNull(message = "NO_FILE_UPLOADED")
    MultipartFile file;

    Gender gender;

    // id genre
    Set<Integer> genreIds;
}
