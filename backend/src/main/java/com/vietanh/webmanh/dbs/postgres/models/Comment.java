package com.vietanh.webmanh.dbs.postgres.models;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "comment")
public class Comment extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer commentId;

    Integer parentCommentId;

    @Column(nullable = false)
    Integer storyId;

    @Column(nullable = false)
    Integer chapterId;

    @Column(nullable = false, columnDefinition = "TEXT")
    String content;

    @Builder.Default
    Integer commentLeft = 0;

    @Builder.Default
    Integer commentRight = 0;

    @Column(columnDefinition = "integer[]")
    List<Integer> likedBy;

    @Column(columnDefinition = "integer[]")
    List<Integer> dislikedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;
}
