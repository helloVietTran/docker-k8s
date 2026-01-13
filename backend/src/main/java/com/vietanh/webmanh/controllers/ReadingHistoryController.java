package com.vietanh.webmanh.controllers;

import com.vietanh.webmanh.dtos.responses.ApiResponse;
import com.vietanh.webmanh.dtos.responses.ReadingHistoryResponse;
import com.vietanh.webmanh.services.ReadingHistoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reading-history")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReadingHistoryController {
    ReadingHistoryService readingHistoryService;

    @PutMapping("/comics/{comicId}/chapters/{chapterId}")
    public ApiResponse<ReadingHistoryResponse> saveReadingHistory(
            @PathVariable Integer comicId,
            @PathVariable Integer chapterId
    ) {

        return ApiResponse.<ReadingHistoryResponse>builder()
                .result(readingHistoryService.saveReadingHistory(comicId, chapterId))
                .build();
    }

    @GetMapping("/my")
    public ApiResponse<List<ReadingHistoryResponse>> getReadingHistory() {

        return ApiResponse.<List<ReadingHistoryResponse>>builder()
                .result(readingHistoryService.getAllByUserId())
                .build();
    }

    @DeleteMapping("/comics/{comicId}")
    public ApiResponse<Void> deleteReadingHistory(
            @PathVariable Integer comicId
    ) {
        readingHistoryService.deleteHistoryByComicId(comicId);

        return ApiResponse.<Void>builder().build();
    }
}
