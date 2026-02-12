package com.vietanh.webmanh.services;

import com.vietanh.webmanh.dtos.responses.FollowComicResponse;

import java.util.List;

public interface FollowService {
    void followComic(Integer comicId);
    void unfollowComic(Integer comicId);
    void togglePriority(Integer comicId);
    List<FollowComicResponse> getMyFollowedComics();
    void toggleNotify(Integer comicId);

    boolean isFollowed(Integer comicId);
}
