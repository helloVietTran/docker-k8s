package com.vietanh.webmanh.services;

import jakarta.servlet.http.HttpServletRequest;

public interface ReadingService{
    void increaseExp(Integer chapterId);
    void increaseView(Integer chapterId, HttpServletRequest request);
}
