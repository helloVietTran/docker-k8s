package com.vietanh.webmanh.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResizeImageOptions {
    ORIGINAL(0, 0, "cover-original.jpg"),
    THUMBNAIL_COVER(150, 200, "cover-150x200.jpg"),
    SLIDER_COVER(250, 440, "cover-250x440.jpg");

    private final int width;
    private final int height;
    private final String imageName;
}