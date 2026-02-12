package com.vietanh.webmanh.dbs.postgres.repositories;

import com.vietanh.webmanh.dbs.postgres.models.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, String> {

    @Query("SELECT t FROM ResetPasswordToken t " +
            "WHERE t.userId = :userId " +
            "AND t.isUsed = false " +
            "AND t.expiryAt > :now " +
            "ORDER BY t.createdAt DESC")
    Optional<ResetPasswordToken> findLatestValidTokenByUserId(@Param("userId") Integer userId,
                                                              @Param("now") Instant now);
}
