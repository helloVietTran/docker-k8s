package com.vietanh.webmanh.dbs.postgres.repositories;

import com.vietanh.webmanh.dbs.postgres.models.DailyViewCountStatistic;
import com.vietanh.webmanh.dbs.postgres.specs.ComicStatisticProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DailyViewCountStatisticRepo extends JpaRepository<DailyViewCountStatistic, Integer> {

    @Modifying
    @Query(value = """
        INSERT INTO daily_view_count_statistic
            (comic_id, view_count_increase, start_statistic_at, end_statistic_at)
        VALUES
            (:comicId, :delta, :startAt, :endAt)
        ON CONFLICT (comic_id, start_statistic_at)
        DO UPDATE SET
            view_count_increase =
                daily_view_count_statistic.view_count_increase + EXCLUDED.view_count_increase
    """, nativeQuery = true)
    void upsertDailyStatistic(
            @Param("comicId") Integer comicId,
            @Param("delta") Long delta,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );


    @Query(value = """
        SELECT
            c.*,
            SUM(d.view_count_increase) AS totalView
        FROM daily_view_count_statistic d
        JOIN comic c
            ON c.comic_id = d.comic_id
        WHERE d.start_statistic_at >= :fromTime
          AND d.start_statistic_at < :toTime
        GROUP BY c.comic_id
        ORDER BY totalView DESC
        LIMIT :limit
    """, nativeQuery = true)
    List<ComicStatisticProjection> findTopComicByTimeRange(
            @Param("fromTime") LocalDateTime fromTime,
            @Param("toTime") LocalDateTime toTime,
            @Param("limit") int limit
    );
}
