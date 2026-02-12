package com.vietanh.webmanh.controllers.management;

import com.vietanh.webmanh.dtos.requests.ChapterRequest;
import com.vietanh.webmanh.dtos.requests.UpdateChapterRequest;
import com.vietanh.webmanh.dtos.responses.ApiResponse;
import com.vietanh.webmanh.dtos.responses.ChapterResponse;
import com.vietanh.webmanh.services.ChapterService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/management/comics") // dùng chung /management/comics
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChapterManagementController {
    ChapterService chapterService;

    @PostMapping("/{comicId}/chapters")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    public ApiResponse<ChapterResponse> createChapter(
            @PathVariable Integer comicId,
            @ModelAttribute ChapterRequest request
    ) {
        return ApiResponse.<ChapterResponse>builder()
                .result(chapterService.createChapter(comicId, request))
                .build();
    }

    @PutMapping("/{comicId}/chapters/{chapterId}")
    public ApiResponse<ChapterResponse> updateChapter(
            @PathVariable Integer comicId,
            @PathVariable Integer chapterId,
            @ModelAttribute UpdateChapterRequest request)
    {

        return ApiResponse.<ChapterResponse>builder()
                .result(chapterService.updateChapter(comicId, chapterId, request))
                .build();
    }

    @DeleteMapping("{comicId}/chapters/{chapterId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    public ApiResponse<Void> deleteChapter(
            @PathVariable Integer comicId,
            @PathVariable Integer chapterId)
    {
        chapterService.deleteChapter(comicId, chapterId);
        return ApiResponse.<Void>builder().build();
    }
}
