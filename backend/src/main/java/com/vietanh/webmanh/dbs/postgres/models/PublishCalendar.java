package com.vietanh.webmanh.dbs.postgres.models;

import com.vietanh.webmanh.constants.PublishStatus;
import com.vietanh.webmanh.constants.PublishTargetType;
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
@Table(name = "publish_calendar")
public class PublishCalendar{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer publishCalendarId;

    @Enumerated(EnumType.STRING)
    PublishTargetType publishTargetType;

    @Column(nullable = false)
    Instant publishAt;

    @Enumerated(EnumType.STRING)
    PublishStatus publishStatus;

    @Column(nullable = false)
    Integer targetId;

    Instant publishedAt;

    @Column(nullable = false)
    Instant createdAt;

    Integer createdBy;
    Integer updatedBy;

    @Builder.Default
    int retryCount = 0;
}
