package com.vietanh.webmanh.mappers;

import com.vietanh.webmanh.dbs.postgres.models.Genre;
import com.vietanh.webmanh.dtos.requests.UpdateGenreRequest;
import com.vietanh.webmanh.dtos.responses.GenreResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(componentModel = "spring")
public interface GenreMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateGenre(@MappingTarget Genre genre, UpdateGenreRequest request);

    GenreResponse toGenreResponse(Genre genre);
}