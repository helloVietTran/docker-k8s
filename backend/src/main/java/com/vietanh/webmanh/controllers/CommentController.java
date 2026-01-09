package com.vietanh.webmanh.controllers;

import com.vietanh.webmanh.constants.AdminDecision;
import com.vietanh.webmanh.dtos.requests.CreateCommentRequest;
import com.vietanh.webmanh.dtos.responses.ApiResponse;
import com.vietanh.webmanh.dtos.responses.ComicResponse;
import com.vietanh.webmanh.dtos.responses.CommentResponse;
import com.vietanh.webmanh.services.CommentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    @PostMapping
    ApiResponse<CommentResponse> createComment(
            @Valid @RequestBody CreateCommentRequest request
    ) {

        return ApiResponse.<CommentResponse>builder()
                .result(commentService.createComment(request))
                .build();
    }


    @GetMapping("/chapters/{chapterId}")
    ApiResponse<List<CommentResponse>> getCommentsByChapterId(
            @PathVariable Integer chapterId
    ) {

        return ApiResponse.<List<CommentResponse>>builder()
                .result(commentService.getCommentsByChapterId(chapterId))
                .build();
    }

    @GetMapping("/{commentId}")
    ApiResponse<List<CommentResponse>> getComments(
            @PathVariable Integer commentId
    ) {

        return ApiResponse.<List<CommentResponse>>builder()
                .result(commentService.findSubtree(commentId))
                .build();
    }


    @DeleteMapping("/{commentId}")
    ApiResponse<Void> deleteComment(
            @PathVariable Integer commentId
    ) {
        commentService.deleteComment(commentId);
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/{commentId}/like")
    ApiResponse<Void> likeComment(@PathVariable Integer commentId) {
        commentService.likeComment(commentId);
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/{commentId}/dislike")
    ApiResponse<Void> dislikeComment(@PathVariable Integer commentId) {
        commentService.dislikeComment(commentId);
        return ApiResponse.<Void>builder().build();
    }


}
