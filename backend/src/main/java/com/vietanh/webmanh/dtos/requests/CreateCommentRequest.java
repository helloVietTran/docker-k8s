package com.vietanh.webmanh.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCommentRequest {
    Integer parentCommentId;

    @NotNull
    Integer storyId;

    @NotNull
    Integer chapterId;

    @NotNull
    String content;
}
