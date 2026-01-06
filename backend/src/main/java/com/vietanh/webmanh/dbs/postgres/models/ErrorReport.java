package com.vietanh.webmanh.dbs.postgres.models;

import java.time.Instant;

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
@Table(name = "error-report",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"story_name", "at_chapter", "type"}
                )
        })
public class ErrorReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer errorReporterId;

    @Column(nullable = false)
    String storyName;

    @Column(nullable = false)
    String atChapter;

    @Column(nullable = false)
    String type;

    @Column(nullable = false)
    String description;

    @Builder.Default
    Boolean isFixed=false;

    Instant createdAt;

    Integer userId;
}
