package com.viettran.reading_story_web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.viettran.reading_story_web.dto.request.StoryRequest;
import com.viettran.reading_story_web.dto.response.StoryResponse;
import com.viettran.reading_story_web.entity.mysql.Story;
import com.viettran.reading_story_web.entity.redis.StoryCache;

@Mapper(componentModel = "spring")
public interface StoryMapper {
    Story toStory(StoryRequest request);

    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "chapters", ignore = true)
    StoryResponse toStoryResponse(Story story);

    void updateStory(@MappingTarget Story story, StoryRequest request);

    StoryResponse toStoryResponse(StoryCache storyCache);

    StoryCache toStoryCache(Story story);
}
