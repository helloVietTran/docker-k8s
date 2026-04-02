package com.viettran.reading_story_web.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.viettran.reading_story_web.dto.request.CommentRequest;
import com.viettran.reading_story_web.dto.request.CommentUpdationRequest;
import com.viettran.reading_story_web.dto.response.ApiResponse;
import com.viettran.reading_story_web.dto.response.CommentResponse;
import com.viettran.reading_story_web.dto.response.PageResponse;
import com.viettran.reading_story_web.service.CommentService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {

    CommentService commentService;

    @PostMapping
    ApiResponse<CommentResponse> createComment(@Valid @RequestBody CommentRequest request) {
        return ApiResponse.<CommentResponse>builder()
                .result(commentService.createComment(request))
                .build();
    }

    @PatchMapping("/{commentId}/users/{userId}")
    ApiResponse<CommentResponse> updateComment(
            @Valid @RequestBody CommentUpdationRequest request,
            @PathVariable String commentId,
            @PathVariable String userId) {
        return ApiResponse.<CommentResponse>builder()
                .result(commentService.updateComment(request, commentId, userId))
                .build();
    }

    @DeleteMapping("/{commentId}")
    ApiResponse<String> deleteComment(@PathVariable String commentId, @PathVariable String userId) {
        commentService.deleteComment(commentId);
        return ApiResponse.<String>builder().result("Comment has been deleted").build();
    }

    @GetMapping("/stories/{storyId}")
    ApiResponse<List<CommentResponse>> getCommentsByStoryId(@PathVariable Integer storyId) {
        return ApiResponse.<List<CommentResponse>>builder()
                .result(commentService.getCommentsTreeByStoryId(storyId))
                .build();
    }

    @GetMapping("/chapters/{chapterId}")
    ApiResponse<List<CommentResponse>> getCommentsByChapterId(
            @PathVariable String chapterId) {

        return ApiResponse.<List<CommentResponse>>builder()
                .result(commentService.getCommentsTreeByChapterId(chapterId))
                .build();
    }

    @GetMapping("/new-comments")
    ApiResponse<PageResponse<CommentResponse>> getNewComment() {
        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .result(commentService.getNewComments())
                .build();
    }

    @GetMapping("/users/{userId}")
    ApiResponse<PageResponse<CommentResponse>> getCommentsByUserId(
            @PathVariable String userId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "15") int size) {
        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .result(commentService.getCommentsByUserId(userId, page, size))
                .build();
    }

    @GetMapping("/my")
    ApiResponse<PageResponse<CommentResponse>> getMyComment() {

        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .result(commentService.getMyComment())
                .build();
    }
}
