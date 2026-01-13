package com.vietanh.webmanh.services.impl;

import com.vietanh.webmanh.constants.ErrorCode;
import com.vietanh.webmanh.dbs.postgres.models.Level;
import com.vietanh.webmanh.dbs.postgres.models.User;
import com.vietanh.webmanh.dbs.postgres.repositories.LevelRepository;
import com.vietanh.webmanh.dbs.postgres.repositories.UserRepository;
import com.vietanh.webmanh.exception.AppException;
import com.vietanh.webmanh.services.LevelService;
import com.vietanh.webmanh.utils.AuthUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LevelServiceImpl implements LevelService {
    LevelRepository levelRepository;
    UserRepository userRepository;

    StringRedisTemplate redisTemplate;

    static final String DUPLICATE_KEY_PATTERN = "cache:duplicate::%d::%d";
    static final String READ_COUNT_KEY_PATTERN = "cache:chapters_read::%d";

    @Override
    @Transactional
    public void increaseExp(Integer chapterId) {
        Integer userId = AuthUtil.getCurrentUserId();

        String duplicateKey = String.format(DUPLICATE_KEY_PATTERN, userId, chapterId);
        String readCountKey = String.format(READ_COUNT_KEY_PATTERN, userId);
        // prevent spam increase exp
        Boolean isRecentRead = redisTemplate.hasKey(duplicateKey);
        if (Boolean.TRUE.equals(isRecentRead)) {
            return;
        }

        String cachedValue = redisTemplate.opsForValue().get(readCountKey);
        if (cachedValue == null) {
            // Cache Miss: update database
            Level level = levelRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        User user = userRepository.findById(userId)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                        return Level.builder()
                                .user(user)
                                .chaptersRead(0)
                                .build();
                    });

            level.increaseChaptersRead(1);
            levelRepository.save(level);

            Duration ttl = Duration.ofHours(1)
                    .plusMinutes(ThreadLocalRandom.current().nextInt(10));

            // set cache
            redisTemplate.opsForValue().set(readCountKey, String.valueOf(level.getChaptersRead()), ttl);
        } else {
            // Cache Hit: increase read count
            redisTemplate.opsForValue().increment(cachedValue);
        }

        // mark this chapter is already read
        redisTemplate.opsForValue().set(duplicateKey, "1", Duration.ofSeconds(30));
    }
}
