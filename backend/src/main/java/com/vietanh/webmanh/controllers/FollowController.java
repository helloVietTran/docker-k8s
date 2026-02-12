package com.vietanh.webmanh.controllers;

import com.vietanh.webmanh.dtos.responses.ApiResponse;
import com.vietanh.webmanh.dtos.responses.FollowComicResponse;
import com.vietanh.webmanh.services.FollowService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FollowController {
    FollowService followService;

    @PostMapping("/comics/{comicId}")
    public ApiResponse<Void> follow(@PathVariable Integer comicId) {
        followService.followComic(comicId);
        return ApiResponse.<Void>builder().message("Followed successfully").build();
    }

    @DeleteMapping("/comics/{comicId}")
    public ApiResponse<Void> unfollow(@PathVariable Integer comicId) {
        followService.unfollowComic(comicId);
        return ApiResponse.<Void>builder().message("Unfollowed successfully").build();
    }

    @PatchMapping("/comics/{comicId}/priority")
    public ApiResponse<Void> togglePriority(@PathVariable Integer comicId) {
        followService.togglePriority(comicId);
        return ApiResponse.<Void>builder().build();
    }


    @PatchMapping("/comics/{comicId}/notify")
    public ApiResponse<Void> toggleNotify(@PathVariable Integer comicId) {
        followService.toggleNotify(comicId);
        return ApiResponse.<Void>builder()
                .message("Notification status updated")
                .build();
    }

    @GetMapping("/my")
    public ApiResponse<List<FollowComicResponse>> getMyFollows() {
        return ApiResponse.<List<FollowComicResponse>>builder()
                .result(followService.getMyFollowedComics())
                .build();
    }


    @GetMapping("/comics/{comicId}/status")
    public ApiResponse<Boolean> checkFollowStatus(@PathVariable Integer comicId) {
        return ApiResponse.<Boolean>builder()
                .result(followService.isFollowed(comicId))
                .build();
    }
}