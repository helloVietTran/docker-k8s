package com.vietanh.webmanh.mappers;

import com.vietanh.webmanh.dbs.postgres.models.Chapter;
import com.vietanh.webmanh.dtos.requests.ChapterRequest;
import com.vietanh.webmanh.dtos.responses.ChapterResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChapterMapper {
    Chapter toChapter(ChapterRequest request);

//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void updateComic(UpdateComicRequest request, @MappingTarget Comic comic);

    @Mapping(target = "chapterImages", ignore = true)
    ChapterResponse toChapterResponse(Chapter comic);
}
