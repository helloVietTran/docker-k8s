package com.vietanh.webmanh.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorReportRequest {
    @NotNull
    String storyName;

    @NotNull
    String atChapter;

    @NotNull
    String type;

    @NotNull
    String description;

    Integer userId;
}
