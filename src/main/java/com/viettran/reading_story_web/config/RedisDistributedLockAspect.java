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
            // Lock đã bị chiếm, skip execution
            log.warn("⏭️  Lock not acquired, skipping execution of: {} (another instance is running)", methodName);
            return null;
        }

        try {
            // Lock acquired, execute method
            log.info("▶️  Executing method: {} with lock: {}", methodName, lockKey);
            return joinPoint.proceed();
        } catch (Exception e) {
            log.error("Error executing method: {} with lock: {}", methodName, lockKey, e);
            throw e;
        } finally {
            redisLockService.releaseLock(lockKey, lockValue);
        }
    }
}
