package com.viettran.reading_story_web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.viettran.reading_story_web.dto.request.AvatarFrameRequest;
import com.viettran.reading_story_web.dto.response.AvatarFrameResponse;
import com.viettran.reading_story_web.entity.mysql.AvatarFrame;

@Mapper(componentModel = "spring")
public interface AvatarFrameMapper {
    AvatarFrame toAvatarFrame(AvatarFrameRequest request);

    AvatarFrameResponse toAvatarFrameResponse(AvatarFrame avatarFrame);

    void updateAvatarFrame(@MappingTarget AvatarFrame avatarFrame, AvatarFrameRequest request);
}
