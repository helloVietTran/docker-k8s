package com.vietanh.webmanh.controllers;

import com.vietanh.webmanh.dtos.responses.ApiResponse;
import com.vietanh.webmanh.services.StatisticService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistic")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticController {

    StatisticService statisticService;

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
