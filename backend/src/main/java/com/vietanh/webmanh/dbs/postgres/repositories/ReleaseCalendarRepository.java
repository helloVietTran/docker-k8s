package com.vietanh.webmanh.dbs.postgres.repositories;

import com.vietanh.webmanh.constants.ReleaseStatus;
import com.vietanh.webmanh.dbs.postgres.models.ReleaseCalendar;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// query data không nên dính quyền
@Repository
public interface ReleaseCalendarRepository extends JpaRepository<ReleaseCalendar, Integer> {

    @Query("""
        SELECT r
        FROM ReleaseCalendar r
        WHERE r.comicId = :comicId
          AND r.releaseStatus = :status
        ORDER BY r.releaseAt ASC
    """)
    List<ReleaseCalendar> findCurrentScheduledRelease(
            @Param("comicId") Integer comicId,
            @Param("status") ReleaseStatus status,
            Pageable pageable
    );
}
