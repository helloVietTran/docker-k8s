package com.viettran.reading_story_web.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoryFilterRequest {
    @NotNull(message = "SOME_FIELD_REQURIED")
    Integer id;

    @NotNull(message = "SOME_FIELD_REQURIED")
    String name;

    @NotNull(message = "SOME_FIELD_REQURIED")
    String type;

    String description;
}
