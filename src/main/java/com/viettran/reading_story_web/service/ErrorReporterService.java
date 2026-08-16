package com.viettran.reading_story_web.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.viettran.reading_story_web.dto.request.ErrorReporterRequest;
import com.viettran.reading_story_web.dto.response.ErrorReporterResponse;
import com.viettran.reading_story_web.entity.mysql.ErrorReporter;
import com.viettran.reading_story_web.mapper.ErrorReporterMapper;
import com.viettran.reading_story_web.repository.jpa.ErrorReporterRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ErrorReporterService {
    ErrorReporterRepository errorReporterRepository;
    ErrorReporterMapper errorReporterMapper;

    public ErrorReporterResponse createErrorReporter(ErrorReporterRequest request) {
        ErrorReporter storyFilter = errorReporterMapper.toErrorReporter(request);
        return errorReporterMapper.toErrorReporterResponse(errorReporterRepository.save(storyFilter));
    }

    public List<ErrorReporterResponse> getAllReporter() {
        return errorReporterRepository.findAll().stream()
                .map(errorReporterMapper::toErrorReporterResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteErrorReporter(Integer id) {
        errorReporterRepository.deleteById(id);
    }
}
