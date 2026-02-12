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

    @Override
    @Transactional
    public Level getOrCreateLevel(Integer userId) {

        return levelRepository.findByUserId(userId)
                .orElseGet(() -> {

                    Level level = new Level();
                    level.setUserId(userId);

                    return levelRepository.save(level);
                });
    }
}
