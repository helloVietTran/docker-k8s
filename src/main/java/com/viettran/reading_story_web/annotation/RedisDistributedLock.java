package com.viettran.reading_story_web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation để apply distributed lock vào scheduled methods
 * Đảm bảo method chỉ chạy trên 1 instance duy nhất
 *
 * Ví dụ:
 * @Scheduled(cron = "0 */5 * * * ?")
 * @RedisDistributedLock(key = "job:chapter:sync", timeout = 300)
 * public void syncAndResetChapterViews() { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisDistributedLock {
	/**
	 * Khóa lock trong Redis
	 * Convention: "job:{job-name}:{action}"
	 */
	String key();

	/**
	 * Thời gian lock tồn tại (tính bằng giây)
	 * Nên set lớn hơn hoặc bằng thời gian job chạy
	 * Default: 300 giây (5 phút)
	 */
	long timeout() default 300;
}
