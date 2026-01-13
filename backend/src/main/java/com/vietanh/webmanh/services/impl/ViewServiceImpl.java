package com.vietanh.webmanh.services.impl;

import com.vietanh.webmanh.constants.ErrorCode;
import com.vietanh.webmanh.dbs.postgres.models.Chapter;
import com.vietanh.webmanh.dbs.postgres.repositories.ChapterRepository;
import com.vietanh.webmanh.exception.AppException;
import com.vietanh.webmanh.services.ViewService;
import com.vietanh.webmanh.utils.ClientUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ViewServiceImpl implements ViewService {
    StringRedisTemplate redisTemplate;
    ChapterRepository chapterRepository;

    static final String DUPLICATE_KEY_PATTERN = "cache:duplicate::%d:%s";
    static final String VIEW_COUNT_KEY_PATTERN = "cache:chapter_view_count::%d";

    @Override
    @Transactional
    public void increaseView(Integer chapterId, HttpServletRequest request) {
        String guestHash = ClientUtil.getClientHash(request);

        String duplicateKey = String.format(DUPLICATE_KEY_PATTERN, chapterId, guestHash);
        String viewCountKey = String.format(VIEW_COUNT_KEY_PATTERN, chapterId);

        String dbLockKey = "chapter:view:db:lock:" + chapterId;
        log.info("Call service");
        if (Boolean.TRUE.equals(redisTemplate.hasKey(duplicateKey))) {
            return;
        }

        String cachedView = redisTemplate.opsForValue().get(viewCountKey);
        // cache hit
        if (cachedView != null) {
            redisTemplate.opsForValue().increment(viewCountKey);
            redisTemplate.opsForValue().set(duplicateKey, "1", Duration.ofMinutes(1));
            return;
        }

        // cache miss → thử lấy DB lock
        Boolean dbLock = redisTemplate.opsForValue()
                .setIfAbsent(dbLockKey, "1", Duration.ofSeconds(5));

        if (Boolean.TRUE.equals(dbLock)) {
            // -> Chỉ 1 request vào đây
            Chapter chapter = chapterRepository.findByIdAndIncrementView(chapterId)
                    .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_EXISTED));

            Duration ttl = Duration.ofHours(1)
                    .plusMinutes(ThreadLocalRandom.current().nextInt(10));

            redisTemplate.opsForValue()
                    .set(viewCountKey, String.valueOf(chapter.getViewCount()), ttl);
        } else {
            // -> Request khác đợi cache
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {}

            redisTemplate.opsForValue().increment(viewCountKey);
        }

        redisTemplate.opsForValue().set(duplicateKey, "1", Duration.ofMinutes(1));
    }

}
