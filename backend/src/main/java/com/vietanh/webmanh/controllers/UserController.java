package com.vietanh.webmanh.controllers;

import com.vietanh.webmanh.dtos.requests.UpdateUserRequest;
import com.vietanh.webmanh.dtos.responses.ApiResponse;
import com.vietanh.webmanh.dtos.responses.PageResponse;
import com.vietanh.webmanh.dtos.responses.UserResponse;
import com.vietanh.webmanh.services.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @GetMapping
    ApiResponse<PageResponse<UserResponse>> getUsers(
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {

        return ApiResponse.<PageResponse<UserResponse>>builder()
                .result(userService.getUsers(pageable))
                .build();
    }

    @GetMapping("/my")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PutMapping("/my")
    ApiResponse<UserResponse> updateMyInfo(
            @ModelAttribute UpdateUserRequest request
            ) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateMyInfo(request))
                .build();
    }
}
