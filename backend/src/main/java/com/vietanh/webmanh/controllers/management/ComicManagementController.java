package com.vietanh.webmanh.controllers.management;

import com.vietanh.webmanh.dtos.requests.ComicRequest;
import com.vietanh.webmanh.dtos.requests.UpdateComicRequest;
import com.vietanh.webmanh.dtos.responses.ApiResponse;
import com.vietanh.webmanh.dtos.responses.ComicResponse;
import com.vietanh.webmanh.services.ComicService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/management/comics")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
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

    @DeleteMapping("/{comicId}")
    public ApiResponse<Void> deleteComic(@PathVariable Integer comicId) {
        comicService.deleteComic(comicId);
        return ApiResponse.<Void>builder().build();
    }
}