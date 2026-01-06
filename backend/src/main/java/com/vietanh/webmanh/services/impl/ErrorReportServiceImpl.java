package com.vietanh.webmanh.services.impl;

import com.vietanh.webmanh.constants.ErrorCode;
import com.vietanh.webmanh.dbs.postgres.models.ErrorReport;
import com.vietanh.webmanh.dbs.postgres.repositories.ErrorReporRepository;
import com.vietanh.webmanh.dtos.requests.ErrorReportRequest;
import com.vietanh.webmanh.dtos.responses.ErrorReportResponse;
import com.vietanh.webmanh.dtos.responses.PageResponse;
import com.vietanh.webmanh.exception.AppException;
import com.vietanh.webmanh.mappers.ErrorReportMapper;
import com.vietanh.webmanh.services.ErrorReportService;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ErrorReportServiceImpl implements ErrorReportService {
    ErrorReporRepository reportRepository;

    ErrorReportMapper reportMapper;

    @Override
    public ErrorReportResponse createErrorReporter(ErrorReportRequest request) {
        boolean existed = reportRepository
                .existsByStoryNameAndAtChapterAndType(
                        request.getStoryName(),
                        request.getAtChapter(),
                        request.getType()
                );

        if (existed) {
            throw new AppException(ErrorCode.ERROR_ALREADY_REPORTED);
        }

        ErrorReport entity = reportMapper.toErrorReporter(request);
        entity.setCreatedAt(Instant.now());
        //notify author or admin
        return reportMapper.toErrorReporterResponse(
                reportRepository.save(entity)
        );
    }

    @Override
    public PageResponse<ErrorReportResponse> getErrorReporters(
            String storyName,
            String type,
            Boolean isFixed,
            Pageable pageable
    ) {
        Specification<ErrorReport> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (storyName != null && !storyName.isEmpty()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("storyName")),
                                "%" + storyName.toLowerCase() + "%"
                        )
                );
            }

            if (type != null && !type.isEmpty()) {
                predicates.add(cb.equal(root.get("type"), type));
            }

            if (isFixed != null) {
                predicates.add(cb.equal(root.get("isFixed"), isFixed));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<ErrorReport> page = reportRepository.findAll(spec, pageable);

        List<ErrorReportResponse> data = page.getContent()
                .stream()
                .map(reportMapper::toErrorReporterResponse)
                .toList();

        return PageResponse.<ErrorReportResponse>builder()
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .data(data)
                .build();
    }

    @Override //fix error
    @PreAuthorize("hasRole('ADMIN')")
    public ErrorReportResponse markAsFixed(Integer errorReporterId) {
        ErrorReport entity = reportRepository.findById(errorReporterId)
                .orElseThrow(() -> new AppException(ErrorCode.ERROR_REPORTER_NOT_EXISTED));
        entity.setIsFixed(true);
        return reportMapper.toErrorReporterResponse(reportRepository.save(entity));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteErrorReporter(Integer errorReporterId) {
        reportRepository.deleteById(errorReporterId);
    }
}
