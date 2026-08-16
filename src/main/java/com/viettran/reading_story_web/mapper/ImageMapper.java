package com.viettran.reading_story_web.mapper;

import org.mapstruct.Mapper;

import com.viettran.reading_story_web.dto.response.ImageResponse;
import com.viettran.reading_story_web.entity.mysql.Image;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    ImageResponse toImageResponse(Image image);
}
