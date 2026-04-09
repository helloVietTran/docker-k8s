package com.viettran.reading_story_web.enums;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("male"),
    FEMALE("female"),
    BOTH("both");

    private final String label;

    Gender(String label) {
        this.label = label;
    }
}
