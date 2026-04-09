package com.viettran.reading_story_web.controller;

import java.io.IOException;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.viettran.reading_story_web.dto.request.StoryRequest;
import com.viettran.reading_story_web.dto.response.*;
import com.viettran.reading_story_web.enums.Gender;
import com.viettran.reading_story_web.service.StoryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/stories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StoryController {
    StoryService storyService;

    @PostMapping
    ApiResponse<StoryResponse> createStory(@Valid @ModelAttribute StoryRequest request) throws IOException {
        return ApiResponse.<StoryResponse>builder()
                .result(storyService.createStory(request))
                .build();
    }

    @GetMapping
    ApiResponse<PageResponse<StoryResponse>> getStories(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "32") int size) {
        return ApiResponse.<PageResponse<StoryResponse>>builder()
                .result(storyService.getStories(page, size))
                .build();
    }

    @GetMapping("/{storyId}")
    ApiResponse<StoryResponse> getStoryById(@PathVariable Integer storyId) {
        return ApiResponse.<StoryResponse>builder()
                .result(storyService.getStoryById(storyId))
                .build();
    }

    @DeleteMapping("/{storyId}")
    ApiResponse<String> deleteStory(@PathVariable Integer storyId) {
        storyService.deleteStory(storyId);
        return ApiResponse.<String>builder().result("Story has been deleted").build();
    }

    @PutMapping("{storyId}")
    ApiResponse<StoryResponse> updateStory(@ModelAttribute StoryRequest request, @PathVariable Integer storyId) {
        return ApiResponse.<StoryResponse>builder()
                .result(storyService.updateStory(request, storyId))
                .build();
    }

    @GetMapping("/search")
    ApiResponse<PageResponse<StoryResponse>> searchStories(
            @RequestParam String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<StoryResponse>>builder()
                .result(storyService.searchStories(keyword, page, size))
                .build();
    }

    @PatchMapping("/{storyId}/rate")
    ApiResponse<StoryResponse> rateStory(@PathVariable int storyId, @RequestParam int point) {
        return ApiResponse.<StoryResponse>builder()
                .result(storyService.rateStory(storyId, point))
                .build();
    }

    @GetMapping("/find-story")
    ApiResponse<List<StoryResponse>> filterStory(
            @RequestParam(required = false) Integer genreCode,
            @RequestParam(defaultValue = "-1") Integer status,
            @RequestParam(defaultValue = "1") Integer sort,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.<List<StoryResponse>>builder()
                .result(storyService.filterStory(genreCode, status, sort, keyword))
                .build();
    }

    @GetMapping("/top-views")
    ApiResponse<List<StoryResponse>> getTop10StoriesByViewCount() {
        return ApiResponse.<List<StoryResponse>>builder()
                .result(storyService.getTop10StoriesByViewCount())
                .build();
    }
    // bảng follow
    @GetMapping("/my-followed-stories")
    ApiResponse<List<FollowResponse>> getFollowedStories() {
        return ApiResponse.<List<FollowResponse>>builder()
                .result(storyService.getFollowedStories())
                .build();
    }

    // query: ?gender=GENDER.MALE
    @GetMapping("/gender")
    ApiResponse<PageResponse<StoryResponse>> getStoriesByGender(
            @RequestParam Gender gender,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "32") int size) {
        return ApiResponse.<PageResponse<StoryResponse>>builder()
                .result(storyService.getStoriesByGender(page, size, gender))
                .build();
    }

    @GetMapping("/hot")
    ApiResponse<PageResponse<StoryResponse>> getHotStories(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "32") int size) {
        return ApiResponse.<PageResponse<StoryResponse>>builder()
                .result(storyService.getHotStories(page, size))
                .build();
    }

    @GetMapping("/my-followed-story/{storyId}")
    ApiResponse<FollowResponse> getFollowedStoryByUserIdAndStoryId(@PathVariable Integer storyId) {
        return ApiResponse.<FollowResponse>builder()
                .result(storyService.getFollowedStoryByUserIdAndStoryId(storyId))
                .build();
    }

    @GetMapping("/find-advanced")
    ApiResponse<List<StoryResponse>> filterAdvanced(
            @RequestParam(required = false) List<Integer> genreCodes,
            @RequestParam(required = false) List<Integer> notGenreCodes,
            @RequestParam(defaultValue = "-1") Integer status,
            @RequestParam(defaultValue = "1") Integer sort,
            @RequestParam(required = false, defaultValue = "0") Integer minChapter,
            @RequestParam(required = false, defaultValue = "-1") Integer gender) {

        return ApiResponse.<List<StoryResponse>>builder()
                .result(storyService.filterAdvanced(genreCodes, notGenreCodes, status, sort, minChapter, gender))
                .build();
    }

    @GetMapping("/featured-stories")
    public ApiResponse<List<StoryResponse>> getFeaturedStories() {
        return ApiResponse.<List<StoryResponse>>builder()
                .result(storyService.getFeaturedStories())
                .build();
    }
}
