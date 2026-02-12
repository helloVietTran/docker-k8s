package com.vietanh.webmanh.mappers;

import com.vietanh.webmanh.dbs.postgres.models.User;
import com.vietanh.webmanh.dtos.requests.RegisterRequest;
import com.vietanh.webmanh.dtos.requests.UpdateUserRequest;
import com.vietanh.webmanh.dtos.responses.UserResponse;
import com.vietanh.webmanh.utils.PathUtil;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(RegisterRequest request);

    @Mapping(target = "avatar", ignore = true)
    UserResponse toUserResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget User user,  UpdateUserRequest request);
}