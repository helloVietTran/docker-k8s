package com.vietanh.webmanh.services;

import com.vietanh.webmanh.dtos.requests.UpdateGenreRequest;
import com.vietanh.webmanh.dtos.responses.GenreResponse;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface GenreService {
    List<GenreResponse> getGenres();

    GenreResponse updateGenre(Integer genreId, @RequestBody UpdateGenreRequest request);
}
