package com.viettran.reading_story_web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.viettran.reading_story_web.dto.request.CommentRequest;
import com.viettran.reading_story_web.dto.request.CommentUpdationRequest;
import com.viettran.reading_story_web.dto.response.CommentResponse;
import com.viettran.reading_story_web.entity.mysql.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment toComment(CommentRequest request);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "story", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CommentResponse toCommentResponse(Comment comment);

    void updateComment(@MappingTarget Comment comment, CommentUpdationRequest request);
}
