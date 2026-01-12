package com.vietanh.webmanh.services;

import com.vietanh.webmanh.dtos.requests.ChapterRequest;
import com.vietanh.webmanh.dtos.requests.UpdateChapterRequest;
import com.vietanh.webmanh.dtos.responses.ChapterResponse;

public interface ChapterService {

    ChapterResponse createChapter(Integer comicId, ChapterRequest request);

    ChapterResponse updateChapter(Integer comicId, Integer chapterId, UpdateChapterRequest request);

    void deleteChapter(Integer comicId, Integer chapterId);

    ChapterResponse getChapterById(Integer chapterId);
}
