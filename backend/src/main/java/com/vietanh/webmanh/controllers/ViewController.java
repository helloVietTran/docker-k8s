package com.vietanh.webmanh.controllers;

import com.vietanh.webmanh.dtos.responses.ApiResponse;
import com.vietanh.webmanh.services.ViewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/views")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ViewController {
    ViewService viewService;

    /**
     * Endpoint tăng view cho chapter.
     */
    @PutMapping("/chapters/{chapterId}")
    public ApiResponse<Void> increaseChapterView(
            @PathVariable Integer chapterId,
            HttpServletRequest request
    ) {
        viewService.increaseView(chapterId, request);

        return ApiResponse.<Void>builder()
                .message("View processed")
                .build();
    }
}