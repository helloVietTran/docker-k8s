package com.viettran.reading_story_web.service;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.viettran.reading_story_web.dto.request.ChapterRequest;
import com.viettran.reading_story_web.dto.response.ChapterResponse;
import com.viettran.reading_story_web.dto.response.ImageResponse;
import com.viettran.reading_story_web.dto.response.PageResponse;
import com.viettran.reading_story_web.entity.mysql.Chapter;
import com.viettran.reading_story_web.entity.mysql.Image;
import com.viettran.reading_story_web.entity.mysql.Story;
import com.viettran.reading_story_web.exception.AppException;
import com.viettran.reading_story_web.exception.ErrorCode;
import com.viettran.reading_story_web.mapper.ChapterMapper;
import com.viettran.reading_story_web.mapper.CommentMapper;
import com.viettran.reading_story_web.mapper.ImageMapper;
import com.viettran.reading_story_web.repository.jpa.ChapterRepository;
import com.viettran.reading_story_web.repository.jpa.ImageRepository;
import com.viettran.reading_story_web.repository.jpa.StoryRepository;
import com.viettran.reading_story_web.utils.DateTimeFormatUtil;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChapterService {
    StringRedisTemplate stringRedisTemplate;

    ChapterRepository chapterRepository;
    StoryRepository storyRepository;
    ImageRepository imageRepository;

    ImageMapper imageMapper;
    ChapterMapper chapterMapper;
    CommentMapper commentMapper;

    FileService fileService;

    DateTimeFormatUtil dateTimeFormatUtil;
    RedisCacheAsideService redisCacheAsideService;

    @NonFinal
    @Value("${app.folder.chapter}")
    protected String CHAPTER_FOLDER;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ChapterResponse createChapter(Integer storyId, ChapterRequest request) throws IOException {
        Chapter chapter = chapterMapper.toChapter(request);

        Story story =
                storyRepository.findById(storyId).orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_EXISTED));
        story.setNewestChapter(story.getNewestChapter() + 1);
        story.setUpdatedAt(Instant.now());

        chapter.setStory(story);

        chapter.setCreatedAt(Instant.now());
        chapter.setUpdatedAt(Instant.now());

        List<Map<String, String>> uploadedList = fileService.uploadFiles(request.getFiles(), CHAPTER_FOLDER);
        List<Image> imageList = new ArrayList<>();

        for (Map<String, String> uploadedImage : uploadedList) {
            Image image = Image.builder()
                    .fileName(uploadedImage.get("fileName"))
                    .fileUrl(uploadedImage.get("fileUrl"))
                    .chapter(chapter)
                    .build();

            imageList.add(image);
        }
        chapter.setImages(imageList);
        chapterRepository.save(chapter);

        storyRepository.save(story);
        redisCacheAsideService.evictAllStoryAndChapterCaches();

        ChapterResponse chapterResponse = chapterMapper.toChapterResponse(chapter);
        chapterResponse.setImgSrcs(chapter.getImages());

        return chapterResponse;
    }

    public List<ChapterResponse> getAllChapters(Integer storyId) {
        String cacheKey = RedisCacheAsideService.chapterAllKey(storyId);
        return redisCacheAsideService.getOrLoad(
                cacheKey,
                new com.fasterxml.jackson.core.type.TypeReference<List<ChapterResponse>>() {},
                () -> chapterRepository.findByStoryIdOrderByChapAsc(storyId).stream()
                        .map(chapterMapper::toChapterResponse)
                        .toList());
    }

    public PageResponse<ChapterResponse> getChapters(Integer storyId, int page, int size) {
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        var pageData = chapterRepository.findAllByStoryId(storyId, pageable);

        List<ChapterResponse> chapterResponseList = pageData.getContent().stream()
                .map(chapter -> {
                    ChapterResponse chapterResponse = chapterMapper.toChapterResponse(chapter);
                    chapterResponse.setUpdatedAt(dateTimeFormatUtil.format(chapter.getUpdatedAt()));
                    chapterResponse.setCreatedAt(dateTimeFormatUtil.format(chapter.getCreatedAt()));
                    return chapterResponse;
                })
                .toList();

        return PageResponse.<ChapterResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(chapterResponseList)
                .build();
    }

    public ChapterResponse getChapter(String chapterId) {
        String cacheKey = RedisCacheAsideService.chapterDetailKey(chapterId);
        return redisCacheAsideService.getOrLoad(cacheKey, ChapterResponse.class, () -> {
            Chapter chapter = chapterRepository
                    .findById(chapterId)
                    .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_EXISTED));
            ChapterResponse chapterResponse = chapterMapper.toChapterResponse(chapter);
            chapterResponse.setUpdatedAt(dateTimeFormatUtil.format(chapter.getUpdatedAt()));
            chapterResponse.setCreatedAt(dateTimeFormatUtil.format(chapter.getCreatedAt()));

            return chapterResponse;
        });
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteChapter(String chapterId) {
        chapterRepository.deleteById(chapterId);
        redisCacheAsideService.evictAllStoryAndChapterCaches();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ChapterResponse updateChapter(String chapterId, ChapterRequest request) throws IOException {
        Chapter chapter = chapterRepository
                .findById(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_EXISTED));

        chapter.setUpdatedAt(Instant.now());
        chapterMapper.updateChapter(chapter, request);

        List<Map<String, String>> uploadedList = fileService.uploadFiles(request.getFiles(), CHAPTER_FOLDER);
        List<Image> imageList = new ArrayList<>();

        for (Map<String, String> uploadedImage : uploadedList) {
            Image image = Image.builder()
                    .fileName(uploadedImage.get("fileName"))
                    .fileUrl(uploadedImage.get("fileUrl"))
                    .build();
            imageList.add(image);
        }

        chapter.setImages(imageList);
        chapterRepository.save(chapter);
        redisCacheAsideService.evictAllStoryAndChapterCaches();

        ChapterResponse chapterResponse = chapterMapper.toChapterResponse(chapter);
        chapterResponse.setComments(chapter.getComments().stream()
                .map(commentMapper::toCommentResponse)
                .toList());
        chapterResponse.setImgSrcs(chapter.getImages());

        return chapterResponse;
    }

    public ChapterResponse getChapterByChap(int storyId, int chap) {
        String cacheKey = RedisCacheAsideService.chapterByChapKey(storyId, chap);
        return redisCacheAsideService.getOrLoad(cacheKey, ChapterResponse.class, () -> {
            Optional<Chapter> chapterOptional = chapterRepository.findByStoryIdAndChap(storyId, chap);

            if (chapterOptional.isEmpty()) throw new AppException(ErrorCode.CHAPTER_NOT_EXISTED);
            Chapter chapter = chapterOptional.get();

            ChapterResponse chapterResponse = chapterMapper.toChapterResponse(chapter);
            chapterResponse.setUpdatedAt(dateTimeFormatUtil.format(chapter.getUpdatedAt()));
            chapterResponse.setCreatedAt(dateTimeFormatUtil.format(chapter.getCreatedAt()));

            return chapterResponse;
        });
    }

    public PageResponse<ImageResponse> getChapterResource(String chapterId, int page, int size) {
        Sort sort = Sort.by("createdAt").ascending();

        Pageable pageable = PageRequest.of(page - 1, size, sort);

        var pageData = imageRepository.findAllByChapterId(chapterId, pageable);

        return PageResponse.<ImageResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(pageData.getContent().stream()
                        .map(imageMapper::toImageResponse)
                        .toList())
                .build();
    }

    public void increaseView(Integer storyId, String chapterId) {
        String storyKey = "story::" + storyId;
        String chapterKey = "chapter::" + chapterId;

        stringRedisTemplate.opsForValue().increment(chapterKey, 1);
        stringRedisTemplate.opsForValue().increment(storyKey, 1);
    }
}
