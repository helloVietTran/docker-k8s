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
    @NotNull(message = "THIS_PROPERTY_IS_NOT_ALLOW_NULL")
    String storyName;

    @NotNull(message = "THIS_PROPERTY_IS_NOT_ALLOW_NULL")
    String atChapter;

    @NotNull(message = "THIS_PROPERTY_IS_NOT_ALLOW_NULL")
    String type;

    @NotNull(message = "THIS_PROPERTY_IS_NOT_ALLOW_NULL")
    String description;

    Integer userId;
}
