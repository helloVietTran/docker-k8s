package com.vietanh.webmanh.controllers;

import com.vietanh.webmanh.dtos.responses.ApiResponse;
import com.vietanh.webmanh.dtos.responses.ReadingHistoryResponse;
import com.vietanh.webmanh.services.ReadingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reading")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReadingController {
    ReadingService readingService;

    @PutMapping("/history/comics/{comicId}/chapters/{chapterId}")
    public ApiResponse<ReadingHistoryResponse> saveReadingHistory(
            @PathVariable Integer comicId,
            @PathVariable Integer chapterId
    ) {

        return ApiResponse.<ReadingHistoryResponse>builder()
                .result(readingService.saveReadingHistory(comicId, chapterId))
                .build();
    }

    @GetMapping("/history/my")
    public ApiResponse<List<ReadingHistoryResponse>> getReadingHistory() {

        return ApiResponse.<List<ReadingHistoryResponse>>builder()
                .result(readingService.getAllByUserId())
                .build();
    }

    @DeleteMapping("/history/comics/{comicId}")
    public ApiResponse<Void> deleteReadingHistory(
            @PathVariable Integer comicId
    ) {
        readingService.deleteHistoryByComicId(comicId);

        return ApiResponse.<Void>builder().build();
    }
}
