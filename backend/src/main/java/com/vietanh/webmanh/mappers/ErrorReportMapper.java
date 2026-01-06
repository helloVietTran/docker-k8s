package com.vietanh.webmanh.mappers;

import com.vietanh.webmanh.dbs.postgres.models.ErrorReport;
import com.vietanh.webmanh.dtos.requests.ErrorReportRequest;
import com.vietanh.webmanh.dtos.responses.ErrorReportResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ErrorReportMapper {
    ErrorReport toErrorReporter(ErrorReportRequest request);

    ErrorReportResponse toErrorReporterResponse(ErrorReport errorReport);
}
