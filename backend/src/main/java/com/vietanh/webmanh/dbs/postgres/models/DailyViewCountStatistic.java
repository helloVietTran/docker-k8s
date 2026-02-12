package com.vietanh.webmanh.dbs.postgres.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "daily_view_count_statistic",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_daily_view_comic_day",
                        columnNames = {"comic_id", "start_statistic_at"}
                )
        }
)
public class DailyViewCountStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer dailyViewCountLogId;


    @Column(nullable = false)
    Integer comicId;

    @Column(nullable = false)
    Long viewCountIncrease;

    @Column(nullable = false)
    LocalDateTime startStatisticAt;

    @Column(nullable = false)
    LocalDateTime endStatisticAt;
}
