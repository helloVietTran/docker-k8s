package com.vietanh.webmanh.mappers;

import com.vietanh.webmanh.dbs.postgres.models.Comic;
import com.vietanh.webmanh.dtos.requests.ComicRequest;
import com.vietanh.webmanh.dtos.requests.UpdateComicRequest;
import com.vietanh.webmanh.dtos.responses.ComicResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ComicMapper {
    Comic toComic(ComicRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateComic(UpdateComicRequest request, @MappingTarget Comic comic);

    @Mapping(target = "coverSrc", ignore = true)
    ComicResponse toComicResponse(Comic comic);
}
