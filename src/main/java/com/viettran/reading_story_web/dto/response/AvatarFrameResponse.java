package com.viettran.reading_story_web.dto.response;

import java.time.Duration;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvatarFrameResponse {
    String id;
    String imgSrc;
    Integer price;
    Duration usageTime;
}
