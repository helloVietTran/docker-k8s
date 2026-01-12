package com.vietanh.webmanh.dtos.responses;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterResponse {
    Integer chapterId;
    String chapterNo;
    String chapterName;

    int viewCount;
    String slug;

    String createdAt;
    String updatedAt;

    List<ChapterImageDTO> chapterImages;
}