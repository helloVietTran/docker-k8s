package com.vietanh.webmanh.dbs.postgres.models;

import com.vietanh.webmanh.constants.ReleaseStatus;
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
@Table(name = "release_calendar")
public class ReleaseCalendar extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer releaseId;

    Integer comicId;

    @Column(nullable = false)
    Integer authorId;

    @Column(nullable = false)
    Instant releaseAt;

    @Enumerated(EnumType.STRING)
    ReleaseStatus releaseStatus;

    Integer updatedBy; // ACTOR (admin hoặc author)
}
