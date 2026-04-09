package com.viettran.reading_story_web.mapper;

import org.mapstruct.*;

import com.viettran.reading_story_web.dto.request.UserCreationRequest;
import com.viettran.reading_story_web.dto.request.UserUpdationRequest;
import com.viettran.reading_story_web.dto.response.UserResponse;
import com.viettran.reading_story_web.entity.mysql.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    @Mapping(target = "level", ignore = true)
    UserResponse toUserResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget User user, UserUpdationRequest request);
}
