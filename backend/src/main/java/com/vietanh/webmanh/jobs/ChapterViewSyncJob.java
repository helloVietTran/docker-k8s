package com.vietanh.webmanh.jobs;

import com.vietanh.webmanh.constants.ErrorCode;
import com.vietanh.webmanh.dbs.postgres.models.Chapter;
import com.vietanh.webmanh.dbs.postgres.repositories.ChapterRepository;
import com.vietanh.webmanh.dbs.postgres.repositories.DailyViewCountStatisticRepo;
import com.vietanh.webmanh.exception.AppException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChapterViewSyncJob {
    StringRedisTemplate redisTemplate;
    ChapterRepository chapterRepository;
    DailyViewCountStatisticRepo dailyStatisticRepo;

    static final String CHAPTER_VIEW_KEY_PATTERN = "chapter:*:view_count";
    static final String CHAPTER_SYNC_LOCK = "sync:chapter:lock";

    //TODO: insert theo batch
    //@Scheduled(cron = "0 0 * * * *")
    @Transactional
    @Scheduled(fixedDelay = 60_000)
    public void syncChapterView() {
        // distributed lock
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(CHAPTER_SYNC_LOCK, "1", Duration.ofSeconds(30));

        if (Boolean.FALSE.equals(locked)) {
            return;
        }

        try {
            Set<String> hotKeys = redisTemplate.keys(CHAPTER_VIEW_KEY_PATTERN);
            if (hotKeys == null || hotKeys.isEmpty()) {
                return;
            }

            for (String hotKey : hotKeys) {
                String coldKey = hotKey + ":cold";

                // atomic
                redisTemplate.rename(hotKey, coldKey);

                String value = redisTemplate.opsForValue().get(coldKey);
                if (value == null) {
                    continue;
                }

                int delta = Integer.parseInt(value);
                Integer chapterId = extractChapterId(hotKey);
                // Sync DB
                chapterRepository.increaseViewCount(chapterId, delta);

                Chapter chapter = chapterRepository.findChapterWithComic(chapterId)
                        .orElseThrow(()-> new AppException(ErrorCode.CHAPTER_NOT_EXISTED));

                LocalDateTime start = LocalDate.now().atStartOfDay();
                LocalDateTime end = start.plusDays(1);

                dailyStatisticRepo.upsertDailyStatistic(
                        chapter.getComic().getComicId(),
                        (long) delta,
                        start,
                        end
                );

                // delete cold key
                redisTemplate.delete(coldKey);
            }
        } catch (Exception e) {
            log.error("Chapter view sync failed", e);
        } finally {
            redisTemplate.delete(CHAPTER_SYNC_LOCK);
        }
    }


    private Integer extractChapterId(String key) {
        // chapter:{chapterId}:view_count
        return Integer.valueOf(key.split(":")[1]);
    }

}
