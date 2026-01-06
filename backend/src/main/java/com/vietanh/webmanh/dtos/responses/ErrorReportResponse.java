package com.vietanh.webmanh.dtos.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorReportResponse {
    Integer errorReporterId;
    String storyName;
    String atChapter;
    String type;
    String description;
    Instant createdAt;

    Integer userId;
}
