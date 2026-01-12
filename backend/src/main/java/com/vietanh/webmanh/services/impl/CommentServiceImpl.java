package com.vietanh.webmanh.services.impl;

import com.vietanh.webmanh.constants.ErrorCode;
import com.vietanh.webmanh.dbs.postgres.models.Comment;
import com.vietanh.webmanh.dbs.postgres.models.User;
import com.vietanh.webmanh.dbs.postgres.repositories.ChapterRepository;
import com.vietanh.webmanh.dbs.postgres.repositories.CommentRepository;
import com.vietanh.webmanh.dbs.postgres.repositories.UserRepository;
import com.vietanh.webmanh.dtos.requests.CreateCommentRequest;
import com.vietanh.webmanh.dtos.responses.CommentResponse;
import com.vietanh.webmanh.exception.AppException;
import com.vietanh.webmanh.mappers.CommentMapper;
import com.vietanh.webmanh.services.CommentService;
import com.vietanh.webmanh.utils.AuthUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImpl implements CommentService {

    CommentRepository commentRepository;
    UserRepository userRepository;
    ChapterRepository chapterRepository;

    CommentMapper commentMapper;

    /**
     * Tạo comment theo Nested Set Model (theo chapterId)
     */
    @Override
    @Transactional
    public CommentResponse createComment(CreateCommentRequest request) {
        Integer userId = AuthUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Comment newComment = commentMapper.toComment(request);

        if(!chapterRepository.existsById(request.getChapterId())){
           throw new AppException(ErrorCode.CHAPTER_NOT_EXISTED);
        }

        newComment.setUser(user);

        if (request.getParentCommentId() == null) {

            int maxRight = commentRepository
                    .findMaxRightByChapterId(request.getChapterId())
                    .orElse(0);

            newComment.setCommentLeft(maxRight + 1);
            newComment.setCommentRight(maxRight + 2);

            return commentMapper.toCommentResponse(
                    commentRepository.save(newComment)
            );
        }

        Comment parentComment = commentRepository.findById(request.getParentCommentId())
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));

        Integer parentRight = parentComment.getCommentRight();

        commentRepository.shiftRightFrom(request.getChapterId(), parentRight);
        commentRepository.shiftLeftFrom(request.getChapterId(), parentRight);

        newComment.setCommentLeft(parentRight);
        newComment.setCommentRight(parentRight + 1);
        newComment.setParentCommentId(parentComment.getCommentId());

        return this.mapWithReaction(
                commentRepository.save(newComment)
        );
    }

    @Override
    public List<CommentResponse> getCommentsByChapterId(Integer chapterId) {

        return commentRepository.findAllByChapterIdOrderByTree(chapterId)
                .stream()
                .map(this::mapWithReaction)
                .toList();
    }

    @Override
    @Transactional
    public void deleteComment(Integer commentId) {
        Integer userId = AuthUtil.getCurrentUserId();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));

        if(!comment.getUser().getUserId().equals(userId))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        Integer left = comment.getCommentLeft();
        Integer right = comment.getCommentRight();
        Integer chapterId = comment.getChapterId();

        int width = right - left + 1;

        // 1. Xóa toàn bộ subtree
        commentRepository.deleteSubtree(chapterId, left, right);

        // 2. Dịch các node bên phải
        commentRepository.shiftLeftAfterDelete(chapterId, right, width);
        commentRepository.shiftRightAfterDelete(chapterId, right, width);
    }

    @Override
    public List<CommentResponse> findSubtree(Integer commentId) {

        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));

        return commentRepository.findSubtree(
                        parentComment.getChapterId(),
                        parentComment.getCommentLeft(),
                        parentComment.getCommentRight()
                )
                .stream()
                .map(this::mapWithReaction)
                .toList();
    }

    @Override
    @Transactional
    public void likeComment(Integer commentId) {

        Integer userId = AuthUtil.getCurrentUserId();

        if(!commentRepository.existsById(commentId)){
            throw new AppException(ErrorCode.COMMENT_NOT_EXISTED);
        }

        Object[] status = commentRepository.getReactStatus(commentId, userId);
        boolean liked = status != null && Boolean.TRUE.equals(status[0]);

        if (liked) {
            commentRepository.removeLike(commentId, userId);
        } else {
            commentRepository.likeComment(commentId, userId);
        }
    }

    @Override
    @Transactional
    public void dislikeComment(Integer commentId) {

        Integer userId = AuthUtil.getCurrentUserId();

        if(!commentRepository.existsById(commentId)){
            throw new AppException(ErrorCode.COMMENT_NOT_EXISTED);
        }

        Object[] status = commentRepository.getReactStatus(commentId, userId);
        boolean disliked = status != null && Boolean.TRUE.equals(status[1]);

        if (disliked) {
            commentRepository.removeDislike(commentId, userId);
        } else {
            commentRepository.dislikeComment(commentId, userId);
        }
    }

    private CommentResponse mapWithReaction(Comment comment) {

        CommentResponse response = commentMapper.toCommentResponse(comment);

        response.setLikeCount(
                comment.getLikedBy() == null ? 0 : comment.getLikedBy().size()
        );

        response.setDislikeCount(
                comment.getDislikedBy() == null ? 0 : comment.getDislikedBy().size()
        );

        return response;
    }
}
