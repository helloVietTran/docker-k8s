package com.vietanh.webmanh.controllers;

import com.vietanh.webmanh.constants.ComicSortType;
import com.vietanh.webmanh.constants.Gender;
import com.vietanh.webmanh.constants.ComicStatus;
import com.vietanh.webmanh.dtos.responses.ApiResponse;
import com.vietanh.webmanh.dtos.responses.ComicResponse;
import com.vietanh.webmanh.dtos.responses.PageResponse;
import com.vietanh.webmanh.services.ComicService;
import com.vietanh.webmanh.services.StatisticService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comics")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ComicController {
    ComicService comicService;
    StatisticService statisticService;

    @GetMapping("/{comicId}")
    public ApiResponse<ComicResponse> getComicById(@PathVariable Integer comicId) {

        return ApiResponse.<ComicResponse>builder()
                .result(comicService.getComicById(comicId))
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<ComicResponse>> searchComics(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Integer> genreCodes,
            @RequestParam(required = false) List<Integer> notGenreCodes,
            @RequestParam(required = false) ComicStatus status,
            @RequestParam(required = false) Integer minChapter,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) ComicSortType sortOption,
            Pageable pageable
    ) {
        return ApiResponse.<PageResponse<ComicResponse>>builder()
                .result(
                        comicService.searchComics(
                                keyword,
                                genreCodes,
                                notGenreCodes,
                                status,
                                minChapter, // số chapter tối thiểu
                                gender,
                                sortOption,
                                pageable
                        )
                )
                .build();
    }

    @GetMapping("/top-comics/day")
    ApiResponse<?> topComicByDay(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ApiResponse.builder()
                .result(statisticService.topComicByDay(limit))
                .build();
    }

    @GetMapping("/top-comics/week")
    ApiResponse<?> topComicByWeek(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ApiResponse.builder()
                .result(statisticService.topComicByWeek(limit))
                .build();
    }

    @GetMapping("/top-comics/month")
    ApiResponse<?> topComicByMonth(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ApiResponse.builder()
                .result(statisticService.topComicByMonth(limit))
                .build();
    }
}
