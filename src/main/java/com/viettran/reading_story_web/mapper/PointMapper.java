package com.viettran.reading_story_web.mapper;

import org.mapstruct.Mapper;

import com.viettran.reading_story_web.dto.response.PointResponse;
import com.viettran.reading_story_web.entity.mysql.Point;

@Mapper(componentModel = "spring")
public interface PointMapper {
    PointResponse toPointResponse(Point point);
}
