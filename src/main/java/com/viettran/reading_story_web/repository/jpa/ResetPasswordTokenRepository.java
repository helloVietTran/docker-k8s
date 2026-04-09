package com.viettran.reading_story_web.repository.jpa;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.viettran.reading_story_web.entity.mysql.ResetPasswordToken;

@Repository
public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {

    Optional<ResetPasswordToken> findFirstByUserIdAndStatusAndExpirationDateAfterOrderByCreatedDateAsc(
            String userId, String status, Instant now);
}
