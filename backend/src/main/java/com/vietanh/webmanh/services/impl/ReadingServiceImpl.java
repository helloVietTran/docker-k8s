package com.vietanh.webmanh.services.impl;

import com.vietanh.webmanh.constants.ErrorCode;
import com.vietanh.webmanh.exception.AppException;
import com.vietanh.webmanh.services.LevelService;
import com.vietanh.webmanh.services.ReadingService;
import com.vietanh.webmanh.utils.AuthUtil;
import com.vietanh.webmanh.utils.ClientUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReadingServiceImpl implements ReadingService {
    StringRedisTemplate redisTemplate;

    static final String USER_CHAPTER_READ_LOCK =
            "user:%s:chapter:%d:read:lock";

    static final String USER_CHAPTER_READ_COUNT_CACHE =
            "level:user:%d:read_count";

    static final String VIEW_COUNT_KEY_PATTERN= "chapter:%d:view_count";

    @Override
    public void increaseExp(Integer chapterId) {
        Integer userId = AuthUtil.getCurrentUserId();

        String lockKey = String.format(USER_CHAPTER_READ_LOCK, userId, chapterId);
        String readCountKey = String.format(USER_CHAPTER_READ_COUNT_CACHE, userId);

        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", Duration.ofMinutes(5));

        if (Boolean.FALSE.equals(locked)) {
            throw new AppException(ErrorCode.ALREADY_READING_BOOK);
        }

        Boolean isFirstRead = redisTemplate.opsForValue()
                .setIfAbsent(
                        readCountKey,
                        "1",
                        Duration.ofHours(1)
                                .plusMinutes(ThreadLocalRandom.current().nextInt(10))
                );

        if (Boolean.FALSE.equals(isFirstRead)) {
            redisTemplate.opsForValue().increment(readCountKey);
        }

    }

    @Override
    public void increaseView(Integer chapterId, HttpServletRequest request) {
        String guestHash = ClientUtil.getClientHash(request);

        String lockKey = String.format(USER_CHAPTER_READ_LOCK, guestHash, chapterId);
        String viewCountKey = String.format(VIEW_COUNT_KEY_PATTERN, chapterId);

        // prevent spam view
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", Duration.ofMinutes(5));

        if (Boolean.FALSE.equals(locked)) {
            throw new AppException(ErrorCode.ALREADY_READING_BOOK);
        }

        // cache miss → set from 1
        Boolean isFirstView = redisTemplate.opsForValue()
                .setIfAbsent(viewCountKey, "1", Duration.ofHours(1)
                        .plusMinutes(ThreadLocalRandom.current().nextInt(10)));

        // cache hit → increase
        if (Boolean.FALSE.equals(isFirstView)) {
            redisTemplate.opsForValue().increment(viewCountKey);
        }
    }
}
