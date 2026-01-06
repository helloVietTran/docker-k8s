package com.vietanh.webmanh.services;

import com.vietanh.webmanh.dtos.requests.ErrorReportRequest;
import com.vietanh.webmanh.dtos.responses.ErrorReportResponse;
import com.vietanh.webmanh.dtos.responses.PageResponse;
import org.springframework.data.domain.Pageable;

public interface ErrorReportService {
    ErrorReportResponse createErrorReporter(ErrorReportRequest request);

    ErrorReportResponse markAsFixed(Integer errorReporterId);

    PageResponse<ErrorReportResponse> getErrorReporters(
            String storyName,
            String type,
            Boolean isFixed,
            Pageable pageable
    );

    void deleteErrorReporter(Integer errorReporterId);
}
