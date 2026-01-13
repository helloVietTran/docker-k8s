package com.vietanh.webmanh.dbs.postgres.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "follow_comic",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "comic_id"})
)
public class FollowComic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(nullable = false)
    Integer userId;

    @Column(nullable = false)
    Integer comicId;

    Instant followedAt;

    Integer lastReadChapterId;

    Boolean notifyEnabled;
}
