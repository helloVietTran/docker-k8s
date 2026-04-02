package com.viettran.reading_story_web.repository.jpa;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.viettran.reading_story_web.entity.mysql.Chapter;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, String> {
    @Query(value = "SELECT * FROM chapter WHERE story_id = :storyId ORDER BY chap DESC LIMIT 3", nativeQuery = true)
    List<Chapter> findTop3ChaptersByStoryId(@Param("storyId") Integer storyId);

    List<Chapter> findByStoryIdOrderByChapAsc(Integer storyId);

    Page<Chapter> findAllByStoryId(Integer storyId, Pageable pageable);

    Optional<Chapter> findByStoryIdAndChap(Integer storyId, Integer chap);

    @Modifying
    @Transactional
    @Query("UPDATE Chapter c SET c.viewCount = c.viewCount + 1 WHERE c.id = :chapterId")
    void incrementViewCount(@Param("chapterId") String chapterId);
}
