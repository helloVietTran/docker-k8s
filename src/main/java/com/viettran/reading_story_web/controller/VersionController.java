package com.viettran.reading_story_web.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viettran.reading_story_web.dto.response.ApiResponse;

@RestController
@RequestMapping("/version")
public class VersionController {

    @Value("${app.version:v1.0.0}")
    private String appVersion;

    @GetMapping
    public ApiResponse<Map<String, Object>> getVersion() {
        return ApiResponse.<Map<String, Object>>builder()
                .result(Map.of(
                        "version", appVersion,
                        "status", "UP",
                        "timestamp", System.currentTimeMillis()
                ))
                .build();
    }
}
