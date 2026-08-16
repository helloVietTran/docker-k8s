package com.viettran.reading_story_web.entity.redis;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@RedisHash(value = "StoryCache", timeToLive = 3600)
public class StoryCache implements Serializable {
    @Id
    int id;

    String name;
    String authorName;
    String imgSrc;
    int viewCount;
    double rate;
    int ratingCount;
    int follower;
    int commentCount;
    String slug;
    Long likeCount;
    boolean hot;
    int newestChapter;
}
