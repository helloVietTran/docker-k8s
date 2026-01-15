package com.vietanh.webmanh.dtos.requests;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePublishCalendarRequest {

    @NotNull
    @Future(message = "PUBLISH_TIMESTAMP_ERROR")
    Instant publishAt;
}
