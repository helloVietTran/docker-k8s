package com.vietanh.webmanh.services;

import com.vietanh.webmanh.dtos.requests.ComicRequest;
import com.vietanh.webmanh.dtos.responses.ComicResponse;
import com.vietanh.webmanh.dtos.responses.PageResponse;

public interface ComicService {
    ComicResponse createComic(ComicRequest request);

    ComicResponse updateComic(ComicRequest request);

    void deleteComic(ComicRequest request);

    PageResponse<ComicResponse> getComics();
}
