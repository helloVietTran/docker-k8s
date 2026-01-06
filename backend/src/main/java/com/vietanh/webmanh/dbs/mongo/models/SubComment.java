package com.vietanh.webmanh.dbs.mongo.models;

import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubComment {
    String id;
    Integer userId;

    String replyTo;
    String content;

    @Builder.Default
    List<Integer> likedBy = new ArrayList<>();

    @Builder.Default
    List<Integer> dislikedBy = new ArrayList<>();

    Instant createdAt;
}
