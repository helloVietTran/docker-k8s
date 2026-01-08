package com.vietanh.webmanh.controllers;

import com.vietanh.webmanh.constants.AdminDecision;
import com.vietanh.webmanh.dtos.responses.ApiResponse;
import com.vietanh.webmanh.dtos.responses.ComicResponse;
import com.vietanh.webmanh.services.AdminService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    AdminService adminService;

    @PatchMapping("/comics/{comicId}")
    ApiResponse<ComicResponse> approveComic(
            @PathVariable Integer comicId,
            @RequestParam(required = false) AdminDecision decision
    ) {

        return ApiResponse.<ComicResponse>builder()
                .result(adminService.approveComic(comicId, decision))
                .build();
    }

}
