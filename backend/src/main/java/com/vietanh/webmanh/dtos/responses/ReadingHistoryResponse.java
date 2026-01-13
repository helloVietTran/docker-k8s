package com.vietanh.webmanh.dtos.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReadingHistoryResponse {
    Integer readingHistoryId;

    Integer userId;
    Integer comicId;

    String genreName;

    String comicName;

    @Builder.Default
    List<Integer> readChapters = new ArrayList<>();

    Instant lastUpdatedAt;
}
