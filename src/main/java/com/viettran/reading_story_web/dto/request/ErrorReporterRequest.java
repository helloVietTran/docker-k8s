package com.viettran.reading_story_web.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorReporterRequest {
    int atChapter;
    String storyName;

    @NotBlank(message = "SOME_FIELDS_REQUIRED")
    String type;

    String description;
}
