package com.vietanh.webmanh.controllers.management;

import com.vietanh.webmanh.dtos.requests.UpdatePublishCalendarRequest;
import com.vietanh.webmanh.dtos.responses.ApiResponse;
import com.vietanh.webmanh.dtos.responses.PublishCalendarResponse;
import com.vietanh.webmanh.services.PublishCalendarService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/management/publish-calendar")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublishManagementController {
    PublishCalendarService publishCalendarService;

    @PostMapping("/comic/{comicId}")
    public ApiResponse<PublishCalendarResponse> updateComicPublishCalendar(
            @PathVariable Integer comicId,
            @RequestBody UpdatePublishCalendarRequest request
    ) {
        return ApiResponse.<PublishCalendarResponse>builder()
                .result(publishCalendarService.updateComicPublishCalendar(comicId, request))
                .build();

    }

    @GetMapping("/comic/{comicId}")
    public ApiResponse<List<PublishCalendarResponse>> getComicPublishCalendar(
            @PathVariable Integer comicId
    ) {
       return ApiResponse.<List<PublishCalendarResponse>>builder()
               .result(publishCalendarService.getComicPublishCalendar(comicId))
               .build();
    }

    // ======================
    // CHAPTER (chưa implement)
    // ======================

    // @PostMapping("/chapter/{chapterId}")
    // public PublishCalendar createChapterPublishCalendar(...) {}

    // @GetMapping("/chapter/{chapterId}")
    // public List<PublishCalendar> getChapterPublishCalendar(...) {}
}