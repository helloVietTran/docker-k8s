package com.viettran.reading_story_web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.viettran.reading_story_web.dto.request.ChapterRequest;
import com.viettran.reading_story_web.dto.response.ChapterResponse;
import com.viettran.reading_story_web.entity.mysql.Chapter;

@Mapper(componentModel = "spring")
public interface ChapterMapper {
    Chapter toChapter(ChapterRequest request);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "imgSrcs", ignore = true)
    ChapterResponse toChapterResponse(Chapter chapter);

    void updateChapter(@MappingTarget Chapter chapter, ChapterRequest request);
}
