package com.vietanh.webmanh.constants;

import lombok.Getter;

@Getter
public enum ReviewStatus {
    APPROVE_PENDING("Chờ duyệt"),
    APPROVED("Đã chấp thuận"),
    REJECTED("Từ chối");

    private final String vi;

    ReviewStatus(String vi) {
        this.vi = vi;
    }

    public String getVi() {
        return vi;
    }
}
