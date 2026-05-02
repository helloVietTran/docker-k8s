package com.viettran.reading_story_web.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

/**
 * Service để quản lý Distributed Lock trên Redis
 * Đảm bảo cron job chỉ chạy trên 1 instance duy nhất trong cluster
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisLockService {
	StringRedisTemplate stringRedisTemplate;

	/**
	 * Thử lấy lock từ Redis
	 *
	 * @param lockKey Khóa lock (ví dụ: "job:chapter:sync:lock")
	 * @param timeoutSeconds Thời gian lock tồn tại (nên bằng hoặc lớn hơn thời gian job chạy)
	 * @return Giá trị lock được tạo (cần dùng để release lock)
	 */
	public String tryAcquireLock(String lockKey, long timeoutSeconds) {
		String lockValue = UUID.randomUUID().toString();

		Boolean acquired = stringRedisTemplate.opsForValue()
			.setIfAbsent(lockKey, lockValue, Duration.ofSeconds(timeoutSeconds));

		if (acquired != null && acquired) {
			log.info("✓ Lock acquired: {} (timeout: {}s)", lockKey, timeoutSeconds);
			return lockValue;
		}

		log.warn("✗ Lock not acquired (already locked): {}", lockKey);
		return null;
	}

	/**
	 * Giải phóng lock (xóa khóa từ Redis)
	 * Chỉ xóa nếu lock value khớp (để tránh xóa lock của instance khác)
	 *
	 * @param lockKey Khóa lock
	 * @param lockValue Giá trị lock được tạo lúc acquire
	 * @return true nếu xóa thành công, false nếu lock value không khớp
	 */
	public boolean releaseLock(String lockKey, String lockValue) {
		if (lockValue == null) {
			return false;
		}

		String currentValue = stringRedisTemplate.opsForValue().get(lockKey);
		if (lockValue.equals(currentValue)) {
			stringRedisTemplate.delete(lockKey);
			log.info("✓ Lock released: {}", lockKey);
			return true;
		}

		log.warn("✗ Lock value mismatch, cannot release: {}", lockKey);
		return false;
	}

	/**
	 * Wrapper method: Thực thi code trong lock, tự động release sau khi xong
	 *
	 * @param lockKey Khóa lock
	 * @param timeoutSeconds Thời gian lock tồn tại
	 * @param task Lambda function chứa logic cần thực thi
	 * @return true nếu lock được acquire và task chạy, false nếu lock đã bị chiếm
	 */
	public boolean executeWithLock(String lockKey, long timeoutSeconds, Runnable task) {
		String lockValue = tryAcquireLock(lockKey, timeoutSeconds);

		if (lockValue == null) {
			return false;
		}

		try {
			task.run();
			return true;
		} catch (Exception e) {
			log.error("Error while executing task with lock: {}", lockKey, e);
			return false;
		} finally {
			releaseLock(lockKey, lockValue);
		}
	}
}
