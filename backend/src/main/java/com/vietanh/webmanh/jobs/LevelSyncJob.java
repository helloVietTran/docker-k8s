package com.vietanh.webmanh.jobs;

import com.vietanh.webmanh.dbs.postgres.models.Level;
import com.vietanh.webmanh.dbs.postgres.repositories.LevelRepository;
import com.vietanh.webmanh.services.LevelService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LevelSyncJob {
    LevelService levelService;
    StringRedisTemplate redisTemplate;
    LevelRepository levelRepository;

    static final String LEVEL_HOT_KEY_PATTERN = "level:user:*:read_count";
    static final String LEVEL_SYNC_LOCK = "sync:level:lock";

    @Transactional
    @Scheduled(fixedDelay = 60_000)
    public void syncLevel() {
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(LEVEL_SYNC_LOCK, "1", Duration.ofSeconds(30));

        if (Boolean.FALSE.equals(locked)) {
            return;
        }

        try {
            Set<String> hotKeys = redisTemplate.keys(LEVEL_HOT_KEY_PATTERN);
            if (hotKeys == null || hotKeys.isEmpty()) {
                return;
            }

            for (String hotKey : hotKeys) {
                String coldKey = hotKey + ":cold";

                redisTemplate.rename(hotKey, coldKey);

                String value = redisTemplate.opsForValue().get(coldKey);
                if (value == null) {
                    continue;
                }

                int delta = Integer.parseInt(value);
                Integer userId = extractUserId(hotKey);

                Level level = levelService.getOrCreateLevel(userId);
                level.increaseChaptersRead(delta);
                levelRepository.save(level);

                redisTemplate.delete(coldKey);
            }
        } catch (Exception e) {
            log.error("Level sync failed", e);
        } finally {
            redisTemplate.delete(LEVEL_SYNC_LOCK);
        }
    }

    private Integer extractUserId(String key) {
        // level:user:{userId}:read_count
        return Integer.valueOf(key.split(":")[2]);
    }
}
