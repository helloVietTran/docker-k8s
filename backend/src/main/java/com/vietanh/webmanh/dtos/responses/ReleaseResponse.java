package com.vietanh.webmanh.dtos.responses;

import com.vietanh.webmanh.constants.ReleaseStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReleaseResponse {
    Integer releaseId;
    Integer comicId;
    Integer chapterId;
    Instant releaseAt;
    Instant releasedAt;
    ReleaseStatus releaseStatus;

    Integer createdBy;
}
