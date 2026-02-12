package com.vietanh.webmanh.controllers.admin;

import com.vietanh.webmanh.constants.ReviewStatus;
import com.vietanh.webmanh.dtos.responses.ApiResponse;
import com.vietanh.webmanh.dtos.responses.ComicResponse;
import com.vietanh.webmanh.services.ReviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/review")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {
    ReviewService reviewService;

    @PatchMapping("/comics/{comicId}")
    ApiResponse<ComicResponse> reviewComic(
            @PathVariable Integer comicId,
            @RequestParam(name = "review") ReviewStatus reviewStatus
    ) {

        return ApiResponse.<ComicResponse>builder()
                .result(reviewService.reviewComic(comicId, reviewStatus))
                .build();
    }

}
