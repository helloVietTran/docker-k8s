package com.vietanh.webmanh.controllers;

import java.util.List;

import com.vietanh.webmanh.dtos.requests.UpdateGenreRequest;
import com.vietanh.webmanh.dtos.responses.ApiResponse;
import com.vietanh.webmanh.dtos.responses.GenreResponse;
import com.vietanh.webmanh.services.GenreService;

import org.springframework.web.bind.annotation.*;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreController {
    GenreService genreService;

    @GetMapping
    public ApiResponse<List<GenreResponse>> getAllGenres() {
        return ApiResponse.<List<GenreResponse>>builder()
                .result(genreService.getGenres())
                .build();
    }

    @PutMapping("/{genreId}")
    public ApiResponse<GenreResponse> updateGenre(@PathVariable Integer genreId, @RequestBody UpdateGenreRequest request) {
        return ApiResponse.<GenreResponse>builder()
                .result(genreService.updateGenre(genreId, request))
                .build();
    }
}