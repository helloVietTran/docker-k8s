package com.viettran.reading_story_web.repository.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.viettran.reading_story_web.entity.mysql.Follow;

@Repository
public interface FollowRepository extends JpaRepository<Follow, String> {

    @Query(
            """
		SELECT DISTINCT f FROM Follow f
		JOIN FETCH f.story s
		LEFT JOIN FETCH s.chapters
		WHERE f.user.id = :userId
	""")
    List<Follow> findFollowedStories(@Param("userId") String userId);

    boolean existsByUserIdAndStoryId(String userId, int storyId);

    Optional<Follow> findByUserIdAndStoryId(String userId, int storyId);
}
