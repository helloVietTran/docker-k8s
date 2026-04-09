package com.viettran.reading_story_web.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.viettran.reading_story_web.dto.request.ErrorReporterRequest;
import com.viettran.reading_story_web.dto.response.ApiResponse;
import com.viettran.reading_story_web.dto.response.ErrorReporterResponse;
import com.viettran.reading_story_web.service.ErrorReporterService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/error-reporter")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ErrorReporterController {
    ErrorReporterService errorReporterService;

    @PostMapping
    ApiResponse<ErrorReporterResponse> createErrorReporter(@Valid @RequestBody ErrorReporterRequest request) {
        return ApiResponse.<ErrorReporterResponse>builder()
                .result(errorReporterService.createErrorReporter(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<ErrorReporterResponse>> getAllReporter() {
        return ApiResponse.<List<ErrorReporterResponse>>builder()
                .result(errorReporterService.getAllReporter())
                .build();
    }

    @DeleteMapping("/{reporterId}")
    ApiResponse<String> deleteErrorReporter(@PathVariable Integer reporterId) {
        errorReporterService.deleteErrorReporter(reporterId);
        return ApiResponse.<String>builder()
                .result("This filter has been deleted")
                .build();
    }
}
