package com.viettran.reading_story_web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.viettran.reading_story_web.dto.request.GenreCreationRequest;
import com.viettran.reading_story_web.dto.request.GenreUpdationRequest;
import com.viettran.reading_story_web.dto.response.GenreResponse;
import com.viettran.reading_story_web.entity.mysql.Genre;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    Genre toGenre(GenreCreationRequest request);

    void updateGenre(@MappingTarget Genre genre, GenreUpdationRequest request);

    GenreResponse toGenreResponse(Genre genre);
}
