package com.vietanh.webmanh.controllers;

import com.vietanh.webmanh.dtos.responses.ApiResponse;
import com.vietanh.webmanh.services.LevelService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/levels")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LevelController {
    LevelService levelService;

    /**
     * Endpoint tăng exp cho người dùng
     */
    @PutMapping("/increase/{chapterId}")
    public ApiResponse<Void> increaseExp(@PathVariable Integer chapterId) {

        levelService.increaseExp(chapterId);

        return ApiResponse.<Void>builder()
                .message("Level progress updated successfully")
                .build();
    }
}
