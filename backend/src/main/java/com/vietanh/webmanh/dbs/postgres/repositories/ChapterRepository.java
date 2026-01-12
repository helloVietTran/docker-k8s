package com.vietanh.webmanh.dbs.postgres.repositories;

import com.vietanh.webmanh.dbs.postgres.models.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Integer> {

    @Query("""
        select distinct c
        from Chapter c
        left join fetch c.chapterImages
        where c.chapterId = :chapterId
    """)
    Optional<Chapter> findByIdWithImages(@Param("chapterId") Integer chapterId);
}
