package com.vietanh.webmanh.controllers;

import com.vietanh.webmanh.dtos.responses.ApiResponse;
import com.vietanh.webmanh.dtos.responses.ChapterResponse;
import com.vietanh.webmanh.services.ChapterService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chapters")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChapterController {
    ChapterService chapterService;

    @GetMapping("/{chapterId}")
    public ApiResponse<ChapterResponse> getChapterById(@PathVariable Integer chapterId) {

        return ApiResponse.<ChapterResponse>builder()
                .result(chapterService.getChapterById(chapterId))
                .build();
    }
}
