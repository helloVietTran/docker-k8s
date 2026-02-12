package com.vietanh.webmanh.mappers;

import com.vietanh.webmanh.dbs.postgres.models.Comment;
import com.vietanh.webmanh.dtos.requests.CreateCommentRequest;
import com.vietanh.webmanh.dtos.responses.CommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment toComment(CreateCommentRequest request);

    CommentResponse toCommentResponse(Comment comment);

}
