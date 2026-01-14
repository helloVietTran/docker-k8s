package com.vietanh.webmanh.jobs;

import com.vietanh.webmanh.dbs.postgres.repositories.ChapterRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChapterViewSyncJob {
    StringRedisTemplate redisTemplate;
    ChapterRepository chapterRepository;

    static final String CHAPTER_VIEW_KEY_PATTERN = "chapter:*:view_count";
    static final String CHAPTER_SYNC_LOCK = "sync:chapter:lock";

    //TODO: insert theo batch
    //@Scheduled(cron = "0 0 * * * *")
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
                log.info("chapterId:: %d", chapterId);
                // Sync DB
                chapterRepository.increaseViewCount(chapterId, delta);

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
