package com.vietanh.webmanh.dtos.responses;

import com.vietanh.webmanh.constants.PublishStatus;
import com.vietanh.webmanh.constants.PublishTargetType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublishCalendarResponse {
    Integer publishCalendarId;
    PublishTargetType publishTargetType;
    Instant publishAt;
    PublishStatus publishStatus;
    Instant publishedAt;
    Instant createdAt;

    Integer createdBy;
    Integer updatedBy;

    int retryCount = 0;
}
