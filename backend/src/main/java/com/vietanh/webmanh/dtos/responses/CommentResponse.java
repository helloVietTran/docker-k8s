package com.vietanh.webmanh.dtos.responses;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
    Integer commentId;
    Integer parentCommentId;
    Integer storyId;
    Integer chapterId;
    String content;

    Integer commentLeft;
    Integer commentRight;

    int likeCount;
    int dislikeCount;
}
