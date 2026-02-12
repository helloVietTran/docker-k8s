package com.vietanh.webmanh.dtos.events;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComicCreatedEvent{
    String topicName;
    Integer comicId;
    String comicName;
    Integer authorId;
    String authorEmail;
    String authorName;
    Instant releaseAt;
}
