package com.vietanh.webmanh.services;

import com.vietanh.webmanh.dtos.requests.UpdateUserRequest;
import com.vietanh.webmanh.dtos.responses.PageResponse;
import com.vietanh.webmanh.dtos.responses.UserResponse;
import org.springframework.data.domain.Pageable;

public interface UserService {

    PageResponse<UserResponse> getUsers(Pageable pageable);

    UserResponse getMyInfo();

    UserResponse updateMyInfo(UpdateUserRequest request);
}
