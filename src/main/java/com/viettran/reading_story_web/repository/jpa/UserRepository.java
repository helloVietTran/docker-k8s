package com.viettran.reading_story_web.repository.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.viettran.reading_story_web.entity.mysql.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findFirstByEmail(String email);

    void deleteByEmail(String email);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query(
            value =
                    """
		DELETE FROM app_user
		WHERE email = :email
		AND id NOT IN (
			SELECT min_id FROM (
			SELECT MIN(id) AS min_id
			FROM app_user
			WHERE email = :email
			GROUP BY email
			) tmp
		)
	""",
            nativeQuery = true)
    void deleteDuplicateUsersByEmail(@Param("email") String email);

    @Query("""
		SELECT u FROM User u
		LEFT JOIN FETCH u.level
		WHERE u.id IN :userIds
	""")
    List<User> findUsersWithLevel(@Param("userIds") List<String> userIds);

    @EntityGraph(attributePaths = {"level"})
    @Query("SELECT u FROM User u ORDER BY u.level.chaptersRead DESC")
    List<User> findTop10UsersByChaptersRead(Pageable pageable);
}
