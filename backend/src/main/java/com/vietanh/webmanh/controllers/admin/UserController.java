package com.vietanh.webmanh.controllers.admin;

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
@RequestMapping("/admin/users")
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
}

