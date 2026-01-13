package com.vietanh.webmanh.services.impl;

import com.vietanh.webmanh.constants.ErrorCode;
import com.vietanh.webmanh.dbs.postgres.models.ReadingHistory;
import com.vietanh.webmanh.dbs.postgres.repositories.ChapterRepository;
import com.vietanh.webmanh.dbs.postgres.repositories.ComicRepository;
import com.vietanh.webmanh.dbs.postgres.repositories.FollowComicRepository;
import com.vietanh.webmanh.dbs.postgres.repositories.ReadingHistoryRepository;
import com.vietanh.webmanh.dtos.responses.ReadingHistoryResponse;
import com.vietanh.webmanh.exception.AppException;
import com.vietanh.webmanh.services.ReadingHistoryService;
import com.vietanh.webmanh.utils.AuthUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReadingHistoryServiceImpl implements ReadingHistoryService {

    ReadingHistoryRepository readingHistoryRepository;
    ComicRepository comicRepository;
    ChapterRepository chapterRepository;
    FollowComicRepository followComicRepository;

    @Override
    @Transactional
    public ReadingHistoryResponse saveReadingHistory(Integer comicId, Integer chapterId) {
        if (!comicRepository.existsById(comicId)) {
            throw new AppException(ErrorCode.COMIC_NOT_EXISTED);
        }
        if (!chapterRepository.existsById(chapterId)) {
            throw new AppException(ErrorCode.CHAPTER_NOT_EXISTED);
        }

        Integer userId = AuthUtil.getCurrentUserId();

        ReadingHistory history = readingHistoryRepository
                .findByUserIdAndComicId(userId, comicId)
                .orElseGet(() -> ReadingHistory.builder()
                        .userId(userId)
                        .comicId(comicId)
                        .readChapters(new ArrayList<>())
                        .build()
                );

        List<Integer> currentChapters = history.getReadChapters();
        if (currentChapters == null) {
            currentChapters = new ArrayList<>();
        }

        if (!currentChapters.contains(chapterId)) {
            currentChapters.add(chapterId);
            history.setReadChapters(currentChapters);
        }

        ReadingHistory saved = readingHistoryRepository.save(history);

        return ReadingHistoryResponse.builder()
                .readingHistoryId(saved.getReadingHistoryId())
                .comicId(saved.getComicId())
                .readChapters(saved.getReadChapters())
                .build();
    }

    @Override
    public List<ReadingHistoryResponse> getAllByUserId() {

        Integer userId = AuthUtil.getCurrentUserId();

        return readingHistoryRepository.findAllByUserId(userId)
                .stream()
                .map(history -> ReadingHistoryResponse.builder()
                        .readingHistoryId(history.getReadingHistoryId())
                        .comicId(history.getComicId())
                        .readChapters(history.getReadChapters())
                        .build()
                )
                .toList();
    }

    @Override
    public void deleteHistoryByComicId(Integer comicId) {

        Integer userId = AuthUtil.getCurrentUserId();

        readingHistoryRepository.deleteByUserIdAndComicId(userId, comicId);
    }
}

