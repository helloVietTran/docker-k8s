package com.viettran.reading_story_web.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReadingHistoryRequest {
    int storyId;

    // chapter đã đọc
    Integer chapterRead;
}
