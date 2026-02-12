package com.vietanh.webmanh.dtos.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateChapterRequest {
    String chapterNo;

    String chapterName;

    List<MultipartFile> imageFiles;
}
