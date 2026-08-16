package com.viettran.reading_story_web.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.viettran.reading_story_web.annotation.RedisDistributedLock;
import com.viettran.reading_story_web.service.RedisLockService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Tự động acquire lock trước khi chạy method, release lock sau khi xong
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedisDistributedLockAspect {
    private final RedisLockService redisLockService;

    @Around("@annotation(lock)")
    public Object aroundRedisDistributedLock(ProceedingJoinPoint joinPoint, RedisDistributedLock lock)
            throws Throwable {

        String methodName = joinPoint.getSignature().getName();
        String lockKey = lock.key();
        long timeout = lock.timeout();

        String lockValue = redisLockService.tryAcquireLock(lockKey, timeout);

        if (lockValue == null) {
            log.warn(
                    "time={} - service=redis-distributed-lock - info=lock - message=Lock not acquired, skipping execution of: {} (another instance is running)",
                    java.time.LocalDateTime.now(),
                    methodName);
            return null;
        }

        try {
            log.info(
                    "time={} - service=redis-distributed-lock - info=lock - message=Executing method: {} with lock: {}",
                    java.time.LocalDateTime.now(),
                    methodName,
                    lockKey);
            return joinPoint.proceed();
        } catch (Exception e) {
            log.error(
                    "time={} - service=redis-distributed-lock - info=lock - message=Error executing method: {} with lock: {}",
                    java.time.LocalDateTime.now(),
                    methodName,
                    lockKey,
                    e);
            throw e;
        } finally {
            redisLockService.releaseLock(lockKey, lockValue);
        }
    }
}
