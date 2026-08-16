package com.viettran.reading_story_web.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.viettran.reading_story_web.dto.response.LevelResponse;
import com.viettran.reading_story_web.entity.mysql.Level;
import com.viettran.reading_story_web.exception.AppException;
import com.viettran.reading_story_web.exception.ErrorCode;
import com.viettran.reading_story_web.mapper.LevelMapper;
import com.viettran.reading_story_web.repository.jpa.LevelRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LevelService {
    LevelRepository levelRepository;

    LevelMapper levelMapper;
    StringRedisTemplate stringRedisTemplate;

    AuthenticationService authenticationService;

    public Long increaseLevel(String chapterId) {
        String userId = authenticationService.getCurrentUserId();
        // kiểm tra user đã đọc chapter chưa
        if (hasUserReadChapter(userId, chapterId)) return null;
        // đánh dấu user đã đọc chapter nào
        markChapterAsReadByThisUser(userId, chapterId);

        return stringRedisTemplate.opsForValue().increment("user::" + userId);
    }

    private boolean hasUserReadChapter(String userId, String chapterId) {
        // key tạm thời đánh dấu ":"
        String key = "user:" + userId + ":chapter:" + chapterId;

        Boolean exists = stringRedisTemplate.hasKey(key);
        return (exists != null && exists);
    }

    private void markChapterAsReadByThisUser(String userId, String chapterId) {
        String key = "user:" + userId + ":chapter:" + chapterId;
        long timestamp = Instant.now().getEpochSecond();
        stringRedisTemplate.opsForValue().set(key, String.valueOf(timestamp), Duration.ofMinutes(5));
    }

    public LevelResponse getUserLevel(String userId) {
        String chaptersRead = stringRedisTemplate.opsForValue().get("user::" + userId);

        Optional<Level> levelOptional = levelRepository.findByUserId(userId);

        Level level;
        if (levelOptional.isEmpty()) throw new AppException(ErrorCode.LEVEL_NOT_EXISTED);

        level = levelOptional.get();

        if (chaptersRead != null) level.increaseChaptersRead(Integer.parseInt(chaptersRead));

        return levelMapper.toLevelResponse(levelRepository.save(level));
    }
}
