package com.vietanh.webmanh.controllers;

import com.vietanh.webmanh.dtos.requests.ErrorReportRequest;
import com.vietanh.webmanh.dtos.responses.ApiResponse;
import com.vietanh.webmanh.dtos.responses.ErrorReportResponse;
import com.vietanh.webmanh.dtos.responses.PageResponse;
import com.vietanh.webmanh.services.ErrorReportService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/error-report")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ErrorReportController {
    ErrorReportService errorReportService;

    @PostMapping
    public ApiResponse<ErrorReportResponse> createErrorReporter(@Valid @RequestBody ErrorReportRequest request) {
        return ApiResponse.<ErrorReportResponse>builder()
                .result(errorReportService.createErrorReporter(request))
                .build();
    }

    @GetMapping
    public PageResponse<ErrorReportResponse> getErrorReporters(
            @RequestParam(required = false) String storyName,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean isFixed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return errorReportService.getErrorReporters(storyName, type, isFixed, pageable);
    }

    @PatchMapping("/{id}/fix")
    public ApiResponse<ErrorReportResponse> markAsFixed(@PathVariable Integer id) {

        return ApiResponse.<ErrorReportResponse>builder()
                .result(errorReportService.markAsFixed(id))
                .build();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        errorReportService.deleteErrorReporter(id);
    }
}
