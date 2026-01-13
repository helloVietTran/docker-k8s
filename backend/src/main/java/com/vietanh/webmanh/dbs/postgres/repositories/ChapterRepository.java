package com.vietanh.webmanh.dbs.postgres.repositories;

import com.vietanh.webmanh.dbs.postgres.models.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    @Query("""
        select c
        from Chapter c
        where c.comic.id = :comicId
          and c.chapterIndex > :currentIndex
        order by c.chapterIndex asc
        limit 1
    """)
    Optional<Chapter> findNextChapter(
            Integer comicId,
            Integer currentIndex
    );

    @Query("""
        select c
        from Chapter c
        where c.comic.id = :comicId
          and c.chapterIndex < :currentIndex
        order by c.chapterIndex desc
        limit 1
    """)
    Optional<Chapter> findPreviousChapter(
            Integer comicId,
            Integer currentIndex
    );

    @Query("""
        select max(c.chapterIndex)
        from Chapter c
        where c.comic.id = :comicId
    """)
    Optional<Integer> findMaxIndexByComicId(@Param("comicId") Integer comicId);

    @Query(value = "UPDATE chapter SET view_count = view_count + 1 " +
            "WHERE chapter_id = :chapterId " +
            "RETURNING *", nativeQuery = true)
    Optional<Chapter> findByIdAndIncrementView(@Param("chapterId") Integer chapterId);
}
