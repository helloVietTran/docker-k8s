package com.vietanh.webmanh.mappers;

import com.vietanh.webmanh.dbs.postgres.models.Comic;
import com.vietanh.webmanh.dtos.requests.ComicRequest;
import com.vietanh.webmanh.dtos.responses.ComicResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ComicMapper {
    Comic toComic(ComicRequest request);
    ComicResponse toComicResponse(Comic comic);
}
