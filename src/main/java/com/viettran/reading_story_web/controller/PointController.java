package com.viettran.reading_story_web.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.viettran.reading_story_web.dto.request.BuyItemsRequest;
import com.viettran.reading_story_web.dto.response.ApiResponse;
import com.viettran.reading_story_web.dto.response.InventoryResponse;
import com.viettran.reading_story_web.dto.response.PointResponse;
import com.viettran.reading_story_web.service.PointService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PointController {
    PointService pointService;

    @PutMapping("/attendance")
    public ApiResponse<Void> attendance() {
        pointService.attendance();
        return ApiResponse.<Void>builder().build();
    }

    @GetMapping("/my")
    public ApiResponse<PointResponse> getPoint() {
        return ApiResponse.<PointResponse>builder()
                .result(pointService.getPoint())
                .build();
    }

    @PostMapping("/buy-item")
    public ApiResponse<InventoryResponse> buyItem(@Valid @RequestBody BuyItemsRequest request) {
        return ApiResponse.<InventoryResponse>builder()
                .result(pointService.buyItem(request))
                .build();
    }
}
