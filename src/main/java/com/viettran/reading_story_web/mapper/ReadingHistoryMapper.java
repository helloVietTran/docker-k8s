package com.viettran.reading_story_web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.viettran.reading_story_web.dto.response.ReadingHistoryResponse;
import com.viettran.reading_story_web.entity.mysql.ReadingHistory;

@Mapper(componentModel = "spring")
public interface ReadingHistoryMapper {
    @Mapping(target = "story", ignore = true)
    ReadingHistoryResponse toReadingHistoryResponse(ReadingHistory readingHistory);
}
