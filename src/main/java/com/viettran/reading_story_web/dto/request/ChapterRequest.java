package com.viettran.reading_story_web.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterRequest {
    int chap;
    String chapterName;

    List<MultipartFile> files;
}
