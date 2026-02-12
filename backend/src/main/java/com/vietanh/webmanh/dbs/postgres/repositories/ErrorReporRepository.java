package com.vietanh.webmanh.dbs.postgres.repositories;

import com.vietanh.webmanh.dbs.postgres.models.ErrorReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorReporRepository extends JpaRepository<ErrorReport, Integer>,
        JpaSpecificationExecutor<ErrorReport> {

    boolean existsByStoryNameAndAtChapterAndType(
            String storyName,
            String atChapter,
            String type
    );
}
