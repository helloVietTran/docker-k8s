package com.viettran.reading_story_web.controller;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.viettran.reading_story_web.dto.request.GenreCreationRequest;
import com.viettran.reading_story_web.dto.request.GenreUpdationRequest;
import com.viettran.reading_story_web.dto.response.ApiResponse;
import com.viettran.reading_story_web.dto.response.GenreResponse;
import com.viettran.reading_story_web.service.GenreService;

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
                .result(genreService.getAllGenres())
                .build();
    }

    @PostMapping
    public ApiResponse<GenreResponse> createGenre(@Valid @RequestBody GenreCreationRequest request) {
        return ApiResponse.<GenreResponse>builder()
                .result(genreService.createGenre(request))
                .build();
    }

    @PostMapping("/list")
    public ApiResponse<List<GenreResponse>> createListGenres(@RequestBody List<GenreCreationRequest> requests) {
        List<GenreResponse> responses =
                requests.stream().map(genreService::createGenre).collect(Collectors.toList());
        return ApiResponse.<List<GenreResponse>>builder().result(responses).build();
    }

    @PutMapping("/{id}")
    public ApiResponse<GenreResponse> updateGenre(@PathVariable Integer id, @RequestBody GenreUpdationRequest request) {
        return ApiResponse.<GenreResponse>builder()
                .result(genreService.updateGenre(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteGenre(@PathVariable Integer id) {
        genreService.deleteGenre(id);
        return ApiResponse.<String>builder().result("Genre has been deleted").build();
    }
}
