package com.viettran.reading_story_web.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.viettran.reading_story_web.dto.request.ReadingHistoryRequest;
import com.viettran.reading_story_web.dto.response.ApiResponse;
import com.viettran.reading_story_web.dto.response.ReadingHistoryResponse;
import com.viettran.reading_story_web.service.ReadingHistoryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reading-history")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReadingHistoryController {
    ReadingHistoryService readingHistoryService;

    @GetMapping("/my")
    ApiResponse<List<ReadingHistoryResponse>> getReadingHistory() {
        return ApiResponse.<List<ReadingHistoryResponse>>builder()
                .result(readingHistoryService.getReadingHistory())
                .build();
    }

    @PutMapping
    ApiResponse<ReadingHistoryResponse> updateReadingHistory(@RequestBody ReadingHistoryRequest request) {
        return ApiResponse.<ReadingHistoryResponse>builder()
                .result(readingHistoryService.updateReadingHistory(request))
                .build();
    }

    @DeleteMapping("/{readingHistoryId}")
    ApiResponse<String> deleteReadingHistory(@PathVariable String readingHistoryId) {
        readingHistoryService.deleteReadingHistory(readingHistoryId);
        return ApiResponse.<String>builder()
                .result("Reading history has been removed")
                .build();
    }
}
