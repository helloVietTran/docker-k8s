package com.viettran.reading_story_web.mapper;

import org.mapstruct.Mapper;

import com.viettran.reading_story_web.dto.response.LevelResponse;
import com.viettran.reading_story_web.entity.mysql.Level;

@Mapper(componentModel = "spring")
public interface LevelMapper {
    LevelResponse toLevelResponse(Level level);
}
