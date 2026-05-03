package com.viettran.reading_story_web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
     */
    long timeout() default 300;
}
