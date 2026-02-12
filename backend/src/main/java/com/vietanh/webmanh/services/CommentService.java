package com.vietanh.webmanh.services;

import com.vietanh.webmanh.dtos.requests.CreateCommentRequest;
import com.vietanh.webmanh.dtos.responses.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse createComment(CreateCommentRequest request);

    List<CommentResponse> getCommentsByChapterId(Integer chapterId);

    List<CommentResponse> findSubtree(Integer commentId);

    void deleteComment(Integer commentId);

    void likeComment(Integer commentId);
    void dislikeComment(Integer commentId);
}
