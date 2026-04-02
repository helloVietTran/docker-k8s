package com.viettran.reading_story_web.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRequest {
    String parentCommentId;

    @NotNull
    int storyId;

    @NotNull
    String content;

    int atChapter;
    String replyTo;
}
