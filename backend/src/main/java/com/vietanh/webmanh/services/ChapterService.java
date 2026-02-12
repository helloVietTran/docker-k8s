package com.vietanh.webmanh.services;

import com.vietanh.webmanh.dtos.requests.ChapterRequest;
import com.vietanh.webmanh.dtos.requests.UpdateChapterRequest;
import com.vietanh.webmanh.dtos.responses.ChapterResponse;

public interface ChapterService {

    ChapterResponse createChapter(Integer comicId, ChapterRequest request);

    ChapterResponse updateChapter(Integer comicId, Integer chapterId, UpdateChapterRequest request);

    void deleteChapter(Integer comicId, Integer chapterId);

    ChapterResponse getChapterById(Integer chapterId);

    /**
     * Validates that a chapter exists.
     *
     * <p>Flow:
     * <ul>
     *   <li>Check chapterId in Redis SET cache</li>
     *   <li>If cache miss, fallback to database</li>
     *   <li>If exists in DB, update Redis SET</li>
     * </ul>
     *
     * @param chapterId id of the chapter to validate
     */
    void validateReadable(Integer chapterId);
}
