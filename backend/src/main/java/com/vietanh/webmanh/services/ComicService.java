package com.vietanh.webmanh.services;

import com.vietanh.webmanh.constants.ComicSortType;
import com.vietanh.webmanh.constants.Gender;
import com.vietanh.webmanh.constants.ComicStatus;
import com.vietanh.webmanh.dtos.requests.ComicRequest;
import com.vietanh.webmanh.dtos.requests.UpdateComicRequest;
import com.vietanh.webmanh.dtos.responses.ComicResponse;
import com.vietanh.webmanh.dtos.responses.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ComicService {
    ComicResponse createComic(ComicRequest request);

    ComicResponse updateComic(UpdateComicRequest request, Integer comicId);

    ComicResponse getComicById(Integer comicId);

    void deleteComic(Integer comicId);

    PageResponse<ComicResponse> searchComics(
            String keyword,
            List<Integer> genreCodes,
            List<Integer> notGenreCodes,
            ComicStatus status,
            Integer minChapter,
            Gender gender,
            ComicSortType sortOption,
            Pageable pageable
    );;
}
