package com.vietanh.webmanh.constants;

public enum ComicSortType {
    VIEWCOUNT,
    NEWEST,
    OLDEST,
    RATING;

    public static ComicSortType from(String value) {
        if (value == null) return NEWEST;

        try {
            return ComicSortType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NEWEST;
        }
    }
}