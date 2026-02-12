package com.vietanh.webmanh.dtos.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
