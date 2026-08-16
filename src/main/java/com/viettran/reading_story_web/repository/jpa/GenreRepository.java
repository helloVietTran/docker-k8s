package com.viettran.reading_story_web.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.viettran.reading_story_web.entity.mysql.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
    List<Genre> findByIdIn(List<Integer> genreIds);

    @Query("SELECT s.id, g.name FROM Story s JOIN s.genres g WHERE s.id IN :storyIds")
    List<Object[]> findGenresByStoryIds(@Param("storyIds") List<Integer> storyIds);
}
