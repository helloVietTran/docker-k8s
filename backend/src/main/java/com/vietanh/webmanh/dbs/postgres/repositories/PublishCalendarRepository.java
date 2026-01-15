package com.vietanh.webmanh.dbs.postgres.repositories;

import com.vietanh.webmanh.constants.PublishStatus;
import com.vietanh.webmanh.constants.PublishTargetType;
import com.vietanh.webmanh.dbs.postgres.models.PublishCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PublishCalendarRepository extends JpaRepository<PublishCalendar, Integer> {
    @Query("""
    SELECT pc
    FROM PublishCalendar pc
    WHERE pc.publishStatus = :status
      AND pc.publishAt <= :now
    """)
    List<PublishCalendar> findDuePublish(
            @Param("status") PublishStatus status,
            @Param("now") Instant now
    );

    @Query("""
        SELECT pc
        FROM PublishCalendar pc
        WHERE pc.publishTargetType = :type
          AND pc.targetId = :targetId
          AND pc.publishStatus = :status
    """)
    List<PublishCalendar> findActiveByTarget(
            @Param("type") PublishTargetType type,
            @Param("targetId") Integer targetId,
            @Param("status") PublishStatus status
    );

    @Query("""
        SELECT pc
        FROM PublishCalendar pc
        WHERE pc.publishTargetType = :type
          AND pc.targetId = :targetId
        ORDER BY pc.createdAt DESC
    """)
    List<PublishCalendar> findByTarget(
            @Param("type") PublishTargetType type,
            @Param("targetId") Integer targetId
    );
}
