package com.vietanh.webmanh.dtos.requests;

import com.vietanh.webmanh.constants.Gender;
import com.vietanh.webmanh.constants.StoryStatus;
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
            value = {StoryStatus.COMPLETED, StoryStatus.ON_GOING},
            message = "STORY_STATUS_ERROR"
    )
    StoryStatus storyStatus;
    Gender gender;
    String description;
    Set<Integer> genreCodes;
    MultipartFile coverImage;
}
