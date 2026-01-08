package com.vietanh.webmanh.controllers;

import com.vietanh.webmanh.constants.ComicSortType;
import com.vietanh.webmanh.constants.Gender;
import com.vietanh.webmanh.constants.StoryStatus;
import com.vietanh.webmanh.dtos.requests.ComicRequest;
import com.vietanh.webmanh.dtos.requests.UpdateComicRequest;
import com.vietanh.webmanh.dtos.responses.ApiResponse;
import com.vietanh.webmanh.dtos.responses.ComicResponse;
import com.vietanh.webmanh.dtos.responses.PageResponse;
import com.vietanh.webmanh.services.ComicService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comics")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ComicController {
    ComicService comicService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ComicResponse> createComic(@ModelAttribute ComicRequest request) {

        return ApiResponse.<ComicResponse>builder()
                .result(comicService.createComic(request))
                .build();
    }

    @PutMapping(value = "/{comicId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ComicResponse> updateComic(
            @PathVariable Integer comicId,
            @ModelAttribute UpdateComicRequest request) {

        return ApiResponse.<ComicResponse>builder()
                .result(comicService.updateComic(request, comicId))
                .build();
    }

    @GetMapping("/{comicId}")
    public ApiResponse<ComicResponse> getComicById(@PathVariable Integer comicId) {

        return ApiResponse.<ComicResponse>builder()
                .result(comicService.getComicById(comicId))
                .build();
    }

    @DeleteMapping("/{comicId}")
    public ApiResponse<Void> deleteComic(@PathVariable Integer comicId) {
        comicService.deleteComic(comicId);
        return ApiResponse.<Void>builder().build();
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<ComicResponse>> searchComics(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Integer> genreCodes,
            @RequestParam(required = false) List<Integer> notGenreCodes,
            @RequestParam(required = false) StoryStatus status,
            @RequestParam(required = false) Integer minChapter,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) ComicSortType sortOption,
            Pageable pageable
    ) {
        return ApiResponse.<PageResponse<ComicResponse>>builder()
                .result(
                        comicService.searchComics(
                                keyword,
                                genreCodes,
                                notGenreCodes,
                                status,
                                minChapter, // số chapter tối thiểu
                                gender,
                                sortOption,
                                pageable
                        )
                )
                .build();
    }

}
