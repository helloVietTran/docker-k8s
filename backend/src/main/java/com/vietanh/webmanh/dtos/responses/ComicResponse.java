package com.vietanh.webmanh.dtos.responses;

import com.vietanh.webmanh.constants.AdminDecision;
import com.vietanh.webmanh.constants.Gender;
import com.vietanh.webmanh.constants.StoryStatus;
import com.vietanh.webmanh.dbs.postgres.models.Genre;
import com.vietanh.webmanh.dbs.postgres.models.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComicResponse {
    Integer comicId;
    String comicName;
    String otherName;
    String authorName;

    StoryStatus status;
    String description;

    int viewCount;
    int totalRatingPoint;
    double ratingPoint;
    int ratingCount;
    int commentCount;
    int followerCount;
    int likeCount;
    int newestChapter;
    Gender gender;
    Set<Genre> genres;

    String slug;

    List<String> coverSrc;

    AdminDecision decision;
}
