package com.vietanh.webmanh.services;

import com.vietanh.webmanh.constants.ReviewStatus;
import com.vietanh.webmanh.dtos.responses.ComicResponse;

public interface ReviewService {
    ComicResponse reviewComic(Integer comicId, ReviewStatus reviewStatus);
}
