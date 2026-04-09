package com.viettran.reading_story_web.repository.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.viettran.reading_story_web.entity.mysql.Level;

@Repository
public interface LevelRepository extends JpaRepository<Level, String> {
    Optional<Level> findByUserId(String userId);
}
