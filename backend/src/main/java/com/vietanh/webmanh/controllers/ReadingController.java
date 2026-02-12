package com.vietanh.webmanh.controllers;

import com.vietanh.webmanh.dtos.responses.ApiResponse;
import com.vietanh.webmanh.services.ChapterService;
import com.vietanh.webmanh.services.ReadingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReadingController {
    ReadingService readingService;
    ChapterService chapterService;

    /**
     * Endpoint tăng exp cho người dùng
     */
    @PutMapping("/level/increase/chapters/{chapterId}")
    public ApiResponse<Void> increaseExp(@PathVariable Integer chapterId) {

        chapterService.validateReadable(chapterId);
        readingService.increaseExp(chapterId);

        return ApiResponse.<Void>builder()
                .message("Level progress updated successfully")
                .build();
    }

    /**
     * Endpoint tăng view cho chapter.
     */
    @PutMapping("/view/increase/chapters/{chapterId}")
    public ApiResponse<Void> increaseChapterView(
            @PathVariable Integer chapterId,
            HttpServletRequest request
    ) {
        chapterService.validateReadable(chapterId);
        readingService.increaseView(chapterId, request);

        return ApiResponse.<Void>builder()
                .message("View processed")
                .build();
    }
}
