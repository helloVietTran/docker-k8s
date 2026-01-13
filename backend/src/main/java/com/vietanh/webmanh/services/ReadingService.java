package com.vietanh.webmanh.services;

import com.vietanh.webmanh.dtos.responses.ReadingHistoryResponse;

import java.util.List;

public interface ReadingService {

    ReadingHistoryResponse saveReadingHistory(Integer comicId, Integer chapterId);

    List<ReadingHistoryResponse> getAllByUserId();

    void deleteHistoryByComicId(Integer comicId);
}
