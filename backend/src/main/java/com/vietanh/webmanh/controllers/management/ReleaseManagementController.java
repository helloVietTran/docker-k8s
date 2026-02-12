package com.vietanh.webmanh.controllers.management;

import com.vietanh.webmanh.dtos.requests.ReleaseUpdateRequest;
import com.vietanh.webmanh.dtos.responses.ApiResponse;
import com.vietanh.webmanh.dtos.responses.ReleaseResponse;
import com.vietanh.webmanh.services.ReleaseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/management/release")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReleaseManagementController {
    ReleaseService releaseService;

    @GetMapping("/comic/{comicId}")
    public ApiResponse<ReleaseResponse> getReleaseComicCalendar(
            @PathVariable Integer comicId
    ) {

        return ApiResponse.<ReleaseResponse>builder()
                .result(releaseService.getReleaseComicCalendar(comicId))
                .build();
    }

    @PatchMapping("/comic/{comicId}")
    public ApiResponse<ReleaseResponse> updateReleaseComicCalendar(
            @PathVariable Integer comicId,
            @RequestBody ReleaseUpdateRequest request
    ) {
        return ApiResponse.<ReleaseResponse>builder()
                .result(releaseService.updateReleaseComicCalendar(comicId, request))
                .build();

    }

    @PatchMapping("/cancel/comic/{comicId}")
    public ApiResponse<ReleaseResponse> cancelReleaseComicCalendar(
            @PathVariable Integer comicId
    ) {
        return ApiResponse.<ReleaseResponse>builder()
                .result(releaseService.cancelReleaseComicCalendar(comicId))
                .build();
    }
}