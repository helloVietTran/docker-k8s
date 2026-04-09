package com.viettran.reading_story_web.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.viettran.reading_story_web.entity.mysql.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, String> {
    Page<Image> findAllByChapterId(String chapterId, Pageable pageable);
}
