package com.viettran.reading_story_web.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.viettran.reading_story_web.dto.response.ReactionResponse;
import com.viettran.reading_story_web.entity.mysql.Comment;
import com.viettran.reading_story_web.entity.mysql.Reaction;
import com.viettran.reading_story_web.entity.mysql.User;
import com.viettran.reading_story_web.enums.ReactionType;
import com.viettran.reading_story_web.exception.AppException;
import com.viettran.reading_story_web.exception.ErrorCode;
import com.viettran.reading_story_web.repository.jpa.CommentRepository;
import com.viettran.reading_story_web.repository.jpa.ReactionRepository;
import com.viettran.reading_story_web.repository.jpa.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReactionService {
    ReactionRepository reactionRepository;
    CommentRepository commentRepository;
    UserRepository userRepository;

    AuthenticationService authenticationService;

    public ReactionResponse toggleLike(String commentId) {
        String userId = authenticationService.getCurrentUserId();
        Optional<Reaction> existingReaction = reactionRepository.findByCommentIdAndUserId(commentId, userId);
        Comment comment = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));

        if (existingReaction.isPresent()) {
            Reaction reaction = existingReaction.get();

            if (reaction.getReactionType() == ReactionType.LIKE) {
                // Nếu đã like -> bỏ like
                reactionRepository.delete(reaction); // xóa bản ghi
                comment.setLikeCount(comment.getLikeCount() - 1); // giảm lượt like
            } else if (reaction.getReactionType() == ReactionType.DISLIKE) {
                // Nếu trước đó là dislike -> chuyển thành like
                reaction.setReactionType(ReactionType.LIKE); // chuyển thành LIKE
                reactionRepository.save(reaction); // lưu lại bản ghi đã cập nhật

                comment.setDislikeCount(comment.getDislikeCount() - 1); // giảm lượt dislike
                comment.setLikeCount(comment.getLikeCount() + 1); // tăng lượt like
            }
        } else {
            // Nếu chưa có reaction -> thêm like
            User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            Reaction newReaction = Reaction.builder()
                    .reactionType(ReactionType.LIKE)
                    .user(user)
                    .comment(comment)
                    .build();

            reactionRepository.save(newReaction);
            comment.setLikeCount(comment.getLikeCount() + 1); // tăng lượt like
        }
        commentRepository.save(comment);

        return ReactionResponse.builder().count(comment.getLikeCount()).build();
    }

    public ReactionResponse toggleDislike(String commentId) {
        String userId = authenticationService.getCurrentUserId();

        Optional<Reaction> existingReaction = reactionRepository.findByCommentIdAndUserId(commentId, userId);

        Comment comment = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));

        if (existingReaction.isPresent()) {
            Reaction reaction = existingReaction.get();

            if (reaction.getReactionType() == ReactionType.DISLIKE) {
                // Nếu đã dislike -> bỏ dislike
                reactionRepository.delete(reaction); // xóa bản ghi
                comment.setDislikeCount(comment.getDislikeCount() - 1); // giảm lượt dislike

            } else if (reaction.getReactionType() == ReactionType.LIKE) {
                // Nếu trước đó là like -> chuyển sang dislike
                reaction.setReactionType(ReactionType.DISLIKE);
                reactionRepository.save(reaction);

                comment.setLikeCount(comment.getLikeCount() - 1); // giảm lượt like
                comment.setDislikeCount(comment.getDislikeCount() + 1); // tăng lượt dislike
            }

        } else {
            // Nếu chưa có reaction -> thêm dislike
            User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            Reaction newReaction = Reaction.builder()
                    .reactionType(ReactionType.DISLIKE)
                    .user(user)
                    .comment(comment)
                    .build();

            reactionRepository.save(newReaction);

            comment.setDislikeCount(comment.getDislikeCount() + 1); // tăng lượt dislike
        }

        commentRepository.save(comment);

        return ReactionResponse.builder().count(comment.getDislikeCount()).build();
    }
}
