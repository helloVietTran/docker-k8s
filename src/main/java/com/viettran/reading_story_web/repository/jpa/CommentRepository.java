package com.viettran.reading_story_web.repository.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.viettran.reading_story_web.entity.mysql.Comment;

import feign.Param;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

	@Query("""
		SELECT c FROM Comment c
		WHERE c.story.id = :storyId
		ORDER BY c.leftVal
	""")
	List<Comment> findAllByStoryOrderByLeft(Integer storyId);

	@Query("""
		SELECT c FROM Comment c
		WHERE c.atChapter= :chapterId
		ORDER BY c.leftVal
	""")
	List<Comment> findAllByChapterOrderByLeft(String chapterId);


    // ================= NESTED SET INSERT =================

    @Query("""
		SELECT MAX(c.rightVal)
		FROM Comment c
		WHERE c.chapter.id = :chapterId
	""")
    Optional<Integer> findMaxRightByChapterId(String chapterId);

    @Modifying
    @Query(
            """
		UPDATE Comment c
		SET c.rightVal = c.rightVal + 2
		WHERE c.chapter.id = :chapterId
		AND c.rightVal >= :right
	""")
    void shiftRightFrom(String chapterId, int right);

    @Modifying
    @Query(
            """
		UPDATE Comment c
		SET c.leftVal = c.leftVal + 2
		WHERE c.chapter.id = :chapterId
		AND c.leftVal > :right
	""")
    void shiftLeftFrom(String chapterId, int right);

    @Modifying
    @Query(
            """
		DELETE FROM Comment c
		WHERE c.chapter.id = :chapterId
		AND c.leftVal >= :left
		AND c.rightVal <= :right
	""")
    void deleteSubtree(@Param("chapterId") String chapterId, @Param("left") int left, @Param("right") int right);

    @Modifying
    @Query(
            """
		UPDATE Comment c
		SET c.leftVal = c.leftVal - :width
		WHERE c.chapter.id = :chapterId
		AND c.leftVal > :right
	""")
    void shiftLeftAfterDelete(
            @Param("chapterId") String chapterId, @Param("right") int right, @Param("width") int width);

    @Modifying
    @Query(
            """
		UPDATE Comment c
		SET c.rightVal = c.rightVal - :width
		WHERE c.chapter.id = :chapterId
		AND c.rightVal > :right
	""")
    void shiftRightAfterDelete(
            @Param("chapterId") String chapterId, @Param("right") int right, @Param("width") int width);

    Optional<Comment> findByIdAndUserId(String commentId, String userId);

    Page<Comment> findAllByUserId(Pageable pageable, String userId);
}
