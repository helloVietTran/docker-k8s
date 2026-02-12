package com.vietanh.webmanh.dbs.postgres.specs;

import java.time.Instant;

public interface ComicStatisticProjection {
    String getComicId();
    String getSlug();
    String getComicName();
    Integer getViewCount();
    Double getRatingPoint();
    Integer getRatingCount();
    Integer getCommentCount();
    Integer getFollowerCount();
    Instant getUpdatedAt();
}
