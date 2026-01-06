package com.vietanh.webmanh.dbs.mongo.models;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "comment")
public class Comment {
    @Id
    String id;

    @Indexed
    Integer userId;

    @Builder.Default
    List<Integer> likedBy = new ArrayList<>();

    @Builder.Default
    List<Integer> dislikedBy = new ArrayList<>();

    @Builder.Default
    List<SubComment> subComments = new ArrayList<>();

    Instant createdAt;
    Instant updatedAt;
}
