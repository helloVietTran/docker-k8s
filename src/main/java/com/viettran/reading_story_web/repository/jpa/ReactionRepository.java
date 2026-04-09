package com.viettran.reading_story_web.repository.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.viettran.reading_story_web.entity.mysql.Reaction;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, String> {
    Optional<Reaction> findByCommentIdAndUserId(String commentId, String userId);
}
