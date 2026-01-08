package com.vietanh.webmanh.dbs.postgres.specs;

import com.vietanh.webmanh.constants.ComicSortType;
import org.springframework.data.domain.Sort;

public class ComicSort {

    private Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");

    private ComicSort() {}

    public static ComicSort builder() {
        return new ComicSort();
    }

    public ComicSort apply(ComicSortType type) {
        if (type == null) return this;
        return switch (type) {
            case VIEWCOUNT -> viewCount();
            case RATING -> rating();
            case NEWEST -> newest();
            case OLDEST -> oldest();
        };
    }

    public ComicSort viewCount() {
        this.sort = Sort.by(Sort.Direction.ASC, "viewCount");
        return this;
    }

    public ComicSort newest() {
        this.sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return this;
    }

    public ComicSort oldest(){
        this.sort = Sort.by(Sort.Direction.ASC, "createdAt");
        return this;
    }

    public ComicSort rating() {
        this.sort = Sort.by(Sort.Direction.DESC, "ratingPoint");
        return this;
    }

    public Sort build() {
        return sort;
    }
}
