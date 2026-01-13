package com.vietanh.webmanh.services;

import jakarta.servlet.http.HttpServletRequest;

public interface ViewService {
    void increaseView(Integer chapterId, HttpServletRequest request);
}
