package com.vietanh.webmanh.mappers;

import com.vietanh.webmanh.dbs.postgres.models.Comic;
import com.vietanh.webmanh.dtos.requests.ComicCreationRequest;
import com.vietanh.webmanh.dtos.requests.ComicUpdateRequest;
import com.vietanh.webmanh.dtos.responses.ComicResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ComicMapper {
    Comic toComic(ComicCreationRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateComic(ComicUpdateRequest request, @MappingTarget Comic comic);

    @Mapping(target = "coverSrc", ignore = true)
    ComicResponse toComicResponse(Comic comic);
}
