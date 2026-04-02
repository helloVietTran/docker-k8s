package com.viettran.reading_story_web.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentResponse {
    String id;
    int atChapter;
    String content;
    String replyTo;
    int likeCount;
    int dislikeCount;

    String createdAt;
    String updatedAt;

    UserResponse user;
    StoryResponse story;

    int rightVal = 0;
    int leftVal = 0;
}
