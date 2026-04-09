package com.viettran.reading_story_web.dto.response;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.viettran.reading_story_web.enums.Gender;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoryResponse {
    int id;
    String name;
    String otherName;
    String authorName;

    String status;
    String description;
    String imgSrc;

    int viewCount;
    double rate;
    int ratingCount;
    int follower;
    int commentCount;
    String slug;
    int newestChapter;
    boolean hot;

    String createdAt;
    String updatedAt;

    Set<String> genres;
    List<ChapterResponse> chapters;
    Gender gender;
}
