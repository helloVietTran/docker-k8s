package com.viettran.reading_story_web.repository.jpa;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.viettran.reading_story_web.entity.mysql.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {

    Optional<Inventory> findFirstByUserIdAndExpirationDateAfter(String userId, Instant instant);

    Boolean existsByUserIdAndExpirationDateAfter(String userId, Instant instant);

    List<Inventory> findByExpirationDateBefore(Instant instant);

    @Query(
            """
	SELECT i FROM Inventory i
	JOIN FETCH i.avatarFrame
	WHERE i.user.id IN :userIds
	AND i.expirationDate > CURRENT_TIMESTAMP
	""")
    List<Inventory> findActiveAvatarFrames(@Param("userIds") List<String> userIds);
}
