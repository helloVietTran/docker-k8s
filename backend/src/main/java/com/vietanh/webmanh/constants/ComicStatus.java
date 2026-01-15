package com.vietanh.webmanh.constants;

public enum ComicStatus {
    ON_GOING("Đang ra"),
    COMPLETED("Hoàn thành"),
    UPCOMING("Sắp phát hành");

    private final String vi;

    ComicStatus(String vi) {
        this.vi = vi;
    }

    public String getVi() {
        return vi;
    }
}
