package com.vietanh.webmanh.controllers;

import com.vietanh.webmanh.dtos.requests.ComicRequest;
import com.vietanh.webmanh.dtos.responses.ApiResponse;
import com.vietanh.webmanh.dtos.responses.ComicResponse;
import com.vietanh.webmanh.services.ComicService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
