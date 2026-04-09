package com.viettran.reading_story_web.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LevelResponse {
    String id;
    String rankName;
    int level;
    float process;
    int chaptersRead;
    int nextLevelChaptersRequired;
}
