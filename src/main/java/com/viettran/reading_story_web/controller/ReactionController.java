package com.viettran.reading_story_web.controller;

import org.springframework.web.bind.annotation.*;

import com.viettran.reading_story_web.dto.response.ApiResponse;
import com.viettran.reading_story_web.dto.response.ReactionResponse;
import com.viettran.reading_story_web.service.ReactionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReactionController {
    ReactionService reactionService;

    @PostMapping("/like/{commentId}")
    ApiResponse<ReactionResponse> toggleLike(@PathVariable String commentId) {
        return ApiResponse.<ReactionResponse>builder()
                .result(reactionService.toggleLike(commentId))
                .build();
    }

    @PostMapping("/dislike/{commentId}")
    ApiResponse<ReactionResponse> toggleDislike(@PathVariable String commentId) {
        return ApiResponse.<ReactionResponse>builder()
                .result(reactionService.toggleDislike(commentId))
                .build();
    }
}
