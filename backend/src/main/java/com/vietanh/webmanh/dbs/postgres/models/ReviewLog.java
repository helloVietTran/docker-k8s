package com.vietanh.webmanh.dbs.postgres.models;

import com.vietanh.webmanh.constants.ReviewStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "review_log")
public class ReviewLog extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer reviewId;

    @Column(nullable = false)
    Integer adminId;

    Integer comicId;

    @Enumerated(EnumType.STRING)
    ReviewStatus reviewStatus;
}
