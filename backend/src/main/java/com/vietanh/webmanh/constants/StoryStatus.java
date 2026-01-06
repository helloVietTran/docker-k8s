package com.vietanh.webmanh.constants;

public enum StoryStatus {

    ON_GOING("Đang ra"),
    COMPLETED("Hoàn thành"),
    UPCOMING("Sắp phát hành");

    private final String vi;

    StoryStatus(String vi) {
        this.vi = vi;
    }

    public String getVi() {
        return vi;
    }
}
