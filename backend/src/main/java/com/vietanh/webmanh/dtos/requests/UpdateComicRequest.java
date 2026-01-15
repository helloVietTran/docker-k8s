package com.vietanh.webmanh.dtos.requests;

import com.vietanh.webmanh.constants.Gender;
import com.vietanh.webmanh.constants.ComicStatus;
import com.vietanh.webmanh.constraints.ValidStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateComicRequest {
    String comicName;
    String otherName;

    @ValidStatus(
            value = {ComicStatus.COMPLETED, ComicStatus.ON_GOING},
            message = "STORY_STATUS_ERROR"
    )
    ComicStatus comicStatus;
    Gender gender;
    String description;
    Set<Integer> genreCodes;
    MultipartFile coverImage;
}
