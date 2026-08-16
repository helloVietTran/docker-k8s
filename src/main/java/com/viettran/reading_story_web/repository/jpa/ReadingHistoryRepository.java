package com.viettran.reading_story_web.repository.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.viettran.reading_story_web.entity.mysql.ReadingHistory;

@Repository
public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, String> {

    List<ReadingHistory> findByUserId(String userId);

    Optional<ReadingHistory> findByUserIdAndStoryId(String userId, Integer StoryId);
}
