package com.viettran.reading_story_web.repository.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.viettran.reading_story_web.entity.mysql.Point;

@Repository
public interface PointRepository extends JpaRepository<Point, String> {
    Optional<Point> findByUserId(String userId);
}
