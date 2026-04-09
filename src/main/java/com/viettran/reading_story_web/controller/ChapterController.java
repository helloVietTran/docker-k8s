package com.viettran.reading_story_web.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.viettran.reading_story_web.dto.request.ChapterRequest;
import com.viettran.reading_story_web.dto.response.ApiResponse;
import com.viettran.reading_story_web.dto.response.ChapterResponse;
import com.viettran.reading_story_web.dto.response.ImageResponse;
import com.viettran.reading_story_web.dto.response.PageResponse;
import com.viettran.reading_story_web.service.ChapterService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stories")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChapterController {
    ChapterService chapterService;

    @PostMapping("/{storyId}/chapters")
    ApiResponse<ChapterResponse> createChapter(@PathVariable Integer storyId, ChapterRequest request)
            throws IOException {
        return ApiResponse.<ChapterResponse>builder()
                .result(chapterService.createChapter(storyId, request))
                .build();
    }

    @GetMapping("/{storyId}/all-chapters")
    ApiResponse<List<ChapterResponse>> getAllChapters(@PathVariable Integer storyId) {
        return ApiResponse.<List<ChapterResponse>>builder()
                .result(chapterService.getAllChapters(storyId))
                .build();
    }

    @GetMapping("/{storyId}/chapters")
    ApiResponse<PageResponse<ChapterResponse>> getChapters(
            @PathVariable Integer storyId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        return ApiResponse.<PageResponse<ChapterResponse>>builder()
                .result(chapterService.getChapters(storyId, page, size))
                .build();
    }

    @GetMapping("/chapters/{chapterId}")
    ApiResponse<ChapterResponse> getChapter(@PathVariable String chapterId) {
        return ApiResponse.<ChapterResponse>builder()
                .result(chapterService.getChapter(chapterId))
                .build();
    }

    @DeleteMapping("/chapters/{chapterId}")
    ApiResponse<String> deleteChapter(@PathVariable String chapterId) {
        chapterService.deleteChapter(chapterId);
        return ApiResponse.<String>builder().result("Chapter is deleted").build();
    }

    @PutMapping("/chapters/{chapterId}")
    ApiResponse<ChapterResponse> updateChapter(@PathVariable String chapterId, @RequestBody ChapterRequest request)
            throws IOException {
        return ApiResponse.<ChapterResponse>builder()
                .result(chapterService.updateChapter(chapterId, request))
                .build();
    }

    @GetMapping("/{storyId}/chap")
    ApiResponse<ChapterResponse> getChapterByChap(@PathVariable Integer storyId, @RequestParam Integer chap) {
        return ApiResponse.<ChapterResponse>builder()
                .result(chapterService.getChapterByChap(storyId, chap))
                .build();
    }

    @GetMapping("/chapters/{chapterId}/resource")
    ApiResponse<PageResponse<ImageResponse>> getChapterResource(
            @PathVariable String chapterId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<ImageResponse>>builder()
                .result(chapterService.getChapterResource(chapterId, page, size))
                .build();
    }

    @PatchMapping("/{storyId}/chapters/{chapterId}/increase-view")
    ApiResponse<String> increaseView(@PathVariable Integer storyId, @PathVariable String chapterId) {
        chapterService.increaseView(storyId, chapterId);
        return ApiResponse.<String>builder()
                .result("Increase view successfully")
                .build();
    }
}
