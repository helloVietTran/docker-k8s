package com.vietanh.webmanh.dtos.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FollowComicResponse {
    Integer followComicId;
    Integer comicId;
    Instant followedAt;
    Boolean priority;
    Boolean notifyEnabled;
}