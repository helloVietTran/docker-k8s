package com.vietanh.webmanh.mappers;

import com.vietanh.webmanh.dbs.postgres.models.User;
import com.vietanh.webmanh.dtos.requests.RegisterRequest;
import com.vietanh.webmanh.dtos.responses.UserResponse;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(RegisterRequest request);

    UserResponse toUserResponse(User user);
}