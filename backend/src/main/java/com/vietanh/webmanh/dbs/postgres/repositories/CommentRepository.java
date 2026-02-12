package com.vietanh.webmanh.dbs.postgres.repositories;

import com.vietanh.webmanh.dbs.postgres.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query("""
        SELECT MAX(c.commentRight)
        FROM Comment c
        WHERE c.chapterId = :chapterId
    """)
    Optional<Integer> findMaxRightByChapterId(
            @Param("chapterId") Integer chapterId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Comment c
        SET c.commentRight = c.commentRight + 2
        WHERE c.chapterId = :chapterId
          AND c.commentRight >= :right
    """)
    void shiftRightFrom(
            @Param("chapterId") Integer chapterId,
            @Param("right") Integer right
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Comment c
        SET c.commentLeft = c.commentLeft + 2
        WHERE c.chapterId = :chapterId
          AND c.commentLeft > :right
    """)
    void shiftLeftFrom(
            @Param("chapterId") Integer chapterId,
            @Param("right") Integer right
    );

    /**
     * Lấy toàn bộ subtree của 1 comment
     */
    @Query("""
        SELECT c
        FROM Comment c
        WHERE c.chapterId = :chapterId
          AND c.commentLeft >= :left
          AND c.commentRight <= :right
        ORDER BY c.commentLeft
    """)
    List<Comment> findSubtree(
            @Param("chapterId") Integer chapterId,
            @Param("left") Integer left,
            @Param("right") Integer right
    );

    /**
     * Lấy toàn bộ comment trong chapter theo thứ tự tree
     */
    @Query("""
        SELECT c
        FROM Comment c
        WHERE c.chapterId = :chapterId
        ORDER BY c.commentLeft
    """)
    List<Comment> findAllByChapterIdOrderByTree(
            @Param("chapterId") Integer chapterId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        DELETE FROM Comment c
        WHERE c.chapterId = :chapterId
          AND c.commentLeft >= :left
          AND c.commentRight <= :right
        """)
    void deleteSubtree(
            @Param("chapterId") Integer chapterId,
            @Param("left") Integer left,
            @Param("right") Integer right
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Comment c
        SET c.commentLeft = c.commentLeft - :width
        WHERE c.chapterId = :chapterId
          AND c.commentLeft > :right
        """)
    void shiftLeftAfterDelete(
            @Param("chapterId") Integer chapterId,
            @Param("right") Integer right,
            @Param("width") Integer width
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Comment c
        SET c.commentRight = c.commentRight - :width
        WHERE c.chapterId = :chapterId
          AND c.commentRight > :right
        """)
    void shiftRightAfterDelete(
            @Param("chapterId") Integer chapterId,
            @Param("right") Integer right,
            @Param("width") Integer width
    );

    @Modifying
    @Query(value = """
        UPDATE comment
        SET liked_by = 
            CASE
                WHEN liked_by IS NULL THEN ARRAY[:userId]
                WHEN NOT (:userId = ANY(liked_by)) THEN array_append(liked_by, :userId)
                ELSE liked_by
            END,
            disliked_by = array_remove(disliked_by, :userId)
        WHERE comment_id = :commentId
    """, nativeQuery = true)
    void likeComment(
            @Param("commentId") Integer commentId,
            @Param("userId") Integer userId
    );

    @Modifying
    @Query(value = """
        UPDATE comment
        SET disliked_by =
            CASE
                WHEN disliked_by IS NULL THEN ARRAY[:userId]
                WHEN NOT (:userId = ANY(disliked_by)) THEN array_append(disliked_by, :userId)
                ELSE disliked_by
            END,
            liked_by = array_remove(liked_by, :userId)
        WHERE comment_id = :commentId
    """, nativeQuery = true)
    void dislikeComment(
            @Param("commentId") Integer commentId,
            @Param("userId") Integer userId
    );

    @Modifying
    @Query(value = """
        UPDATE comment
        SET liked_by = array_remove(liked_by, :userId)
        WHERE comment_id = :commentId
    """, nativeQuery = true)
    void removeLike(
            @Param("commentId") Integer commentId,
            @Param("userId") Integer userId
    );

    @Modifying
    @Query(value = """
        UPDATE comment
        SET disliked_by = array_remove(disliked_by, :userId)
        WHERE comment_id = :commentId
    """, nativeQuery = true)
    void removeDislike(
            @Param("commentId") Integer commentId,
            @Param("userId") Integer userId
    );


    @Query(value = """
        SELECT 
            :userId = ANY(liked_by) AS liked,
            :userId = ANY(disliked_by) AS disliked
        FROM comment
        WHERE comment_id = :commentId
    """, nativeQuery = true)
    Object[] getReactStatus(
            @Param("commentId") Integer commentId,
            @Param("userId") Integer userId
    );

}
