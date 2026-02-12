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
        name = "chapter_purchase",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"userId", "chapterId"})
        }
)
public class ChapterPurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(nullable = false)
    Integer userId;

    @Column(nullable = false)
    Integer chapterId;

    @Column(nullable = false)
    Integer priceCoin;

    @Column(nullable = false)
    Instant purchasedAt;
}
