package com.viettran.reading_story_web.scheduler;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.viettran.reading_story_web.annotation.RedisDistributedLock;
import com.viettran.reading_story_web.entity.mysql.Level;
import com.viettran.reading_story_web.repository.jpa.LevelRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LevelJobScheduler {
    StringRedisTemplate stringRedisTemplate;
    LevelRepository levelRepository;

    @Scheduled(cron = "0 0 * * * ?")
    @RedisDistributedLock(key = "job:level:sync:chapters", timeout = 300)
    protected void syncChaptersReadDataInRedis() {
        log.info(
                "time={} - service=level-job-scheduler - info=job - message=Syncing chapters read data from Redis...",
                java.time.LocalDateTime.now());
        Set<String> keys = stringRedisTemplate.keys("user::*");

        if (keys != null) {
            for (String key : keys) {
                String chaptersReadStr =
                        (String) stringRedisTemplate.opsForValue().get(key);

                if (chaptersReadStr != null) {
                    String[] keyParts = key.split("::");
                    if (keyParts.length == 2) {
                        String userId = keyParts[1];

                        updateLevels(userId, chaptersReadStr);
                        stringRedisTemplate.opsForValue().set(key, "0");
                        log.debug(
                                "time={} - service=level-job-scheduler - info=job - message=Updated level for user: {}",
                                java.time.LocalDateTime.now(),
                                userId);
                    }
                }
            }
        }
    }

    void updateLevels(String userId, String chaptersRead) {
        int chaptersReadNumber = Integer.parseInt(chaptersRead);
        Optional<Level> levelOptional = levelRepository.findByUserId(userId);

        if (levelOptional.isPresent()) {
            Level level = levelOptional.get();
            level.increaseChaptersRead(chaptersReadNumber);
            levelRepository.save(level);
            log.debug(
                    "time={} - service=level-job-scheduler - info=job - message=Level increased for user: {} by {} chapters",
                    java.time.LocalDateTime.now(),
                    userId,
                    chaptersReadNumber);
        }
    }
}
