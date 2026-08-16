package com.viettran.reading_story_web.service;

import java.time.Duration;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisCacheAsideService {
    private static final Logger log = LoggerFactory.getLogger(RedisCacheAsideService.class);
    private static final Duration BASE_TTL = Duration.ofMinutes(1);
    private static final Duration MAX_JITTER = Duration.ofMinutes(1);

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public static Duration randomTtl(Duration base, Duration jitter) {
        long minSeconds = base.getSeconds();
        long maxSeconds = base.plus(jitter).getSeconds();
        long range = Math.max(1L, maxSeconds - minSeconds + 1);
        long randomSeconds = minSeconds + new Random().nextLong(range);
        return Duration.ofSeconds(randomSeconds);
    }

    public static String storyDetailKey(Integer storyId) {
        return "cache:story:detail:" + storyId;
    }

    public static String storyListKey(String suffix) {
        return "cache:story:list:" + suffix;
    }

    public static String storySearchKey(String keyword, int page, int size) {
        return "cache:story:search:" + normalizeKey(keyword) + ":page:" + page + ":size:" + size;
    }

    public static String storyGenderKey(String gender, int page, int size) {
        return "cache:story:gender:" + normalizeKey(gender) + ":page:" + page + ":size:" + size;
    }

    public static String storyHotKey(int page, int size) {
        return "cache:story:hot:page:" + page + ":size:" + size;
    }

    public static String chapterDetailKey(String chapterId) {
        return "cache:chapter:detail:" + chapterId;
    }

    public static String chapterListKey(Integer storyId, int page, int size) {
        return "cache:chapter:list:story:" + storyId + ":page:" + page + ":size:" + size;
    }

    public static String chapterAllKey(Integer storyId) {
        return "cache:chapter:all:story:" + storyId;
    }

    public static String chapterByChapKey(Integer storyId, Integer chap) {
        return "cache:chapter:byChap:story:" + storyId + ":chap:" + chap;
    }

    public void set(String key, Object value) {
        long start = System.currentTimeMillis();
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, randomTtl(BASE_TTL, MAX_JITTER));
        } catch (JsonProcessingException e) {
            log.error("time={} - service=redis-cache-aside - info=cache - message=Cache serialization failed cacheName={} cacheKey={} eventType=ERROR",
                    java.time.LocalDateTime.now(), extractCacheName(key), key, e);
            return;
        }
        long executionTimeMs = System.currentTimeMillis() - start;
        log.info(
                "time={} - service=redis-cache-aside - info=cache - message=Cache saved cacheName={} cacheKey={} executionTimeMs={} eventType=SET",
                java.time.LocalDateTime.now(),
                extractCacheName(key),
                key,
                executionTimeMs);
    }

    public <T> T getOrLoad(String key, Class<T> type, Supplier<T> loader) {
        long start = System.currentTimeMillis();
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            try {
                T value = cached instanceof String json
                        ? objectMapper.readValue(json, type)
                        : type.isInstance(cached) ? type.cast(cached) : objectMapper.convertValue(cached, type);
                long executionTimeMs = System.currentTimeMillis() - start;
                log.info(
                        "time={} - service=redis-cache-aside - info=cache - message=Cache hit cacheName={} cacheKey={} executionTimeMs={} eventType=HIT",
                        java.time.LocalDateTime.now(),
                        extractCacheName(key),
                        key,
                        executionTimeMs);
                return value;
            } catch (IllegalArgumentException | JsonProcessingException ex) {
                log.warn(
                        "time={} - service=redis-cache-aside - info=cache - message=Cache payload does not match expected type cacheName={} cacheKey={} eventType=MISMATCH",
                        java.time.LocalDateTime.now(),
                        extractCacheName(key),
                        key);
            }
        }

        return loadAndCache(key, loader, start);
    }

    public <T> T getOrLoad(String key, TypeReference<T> typeReference, Supplier<T> loader) {
        long start = System.currentTimeMillis();
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            try {
                T value = cached instanceof String json
                        ? objectMapper.readValue(json, typeReference)
                        : objectMapper.convertValue(cached, typeReference);
                long executionTimeMs = System.currentTimeMillis() - start;
                log.info(
                        "time={} - service=redis-cache-aside - info=cache - message=Cache hit cacheName={} cacheKey={} executionTimeMs={} eventType=HIT",
                        java.time.LocalDateTime.now(),
                        extractCacheName(key),
                        key,
                        executionTimeMs);
                return value;
            } catch (IllegalArgumentException | JsonProcessingException ex) {
                log.warn(
                        "time={} - service=redis-cache-aside - info=cache - message=Cache payload does not match expected type cacheName={} cacheKey={} eventType=MISMATCH",
                        java.time.LocalDateTime.now(),
                        extractCacheName(key),
                        key);
            }
        }

        return loadAndCache(key, loader, start);
    }

    private <T> T loadAndCache(String key, Supplier<T> loader, long startTimeMs) {
        long missStart = System.currentTimeMillis();
        T value = loader.get();
        if (value != null) {
            set(key, value);
        }
        long executionTimeMs = System.currentTimeMillis() - missStart;
        log.info(
                "time={} - service=redis-cache-aside - info=cache - message=Cache miss cacheName={} cacheKey={} executionTimeMs={} eventType=MISS",
                java.time.LocalDateTime.now(),
                extractCacheName(key),
                key,
                executionTimeMs);
        return value;
    }

    public void evictStoryCaches() {
        evictByPrefix("cache:story:");
    }

    public void evictChapterCaches() {
        evictByPrefix("cache:chapter:");
    }

    public void evictAllStoryAndChapterCaches() {
        evictStoryCaches();
        evictChapterCaches();
    }

    private void evictByPrefix(String prefix) {
        long start = System.currentTimeMillis();
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        long executionTimeMs = System.currentTimeMillis() - start;
        log.info(
                "time={} - service=redis-cache-aside - info=cache - message=Cache evicted cacheName={} cacheKey={} executionTimeMs={} eventType=EVICT",
                java.time.LocalDateTime.now(),
                prefix,
                keys == null ? "" : keys.toString(),
                executionTimeMs);
    }

    private static String extractCacheName(String cacheKey) {
        if (cacheKey == null || cacheKey.isBlank()) {
            return "unknown";
        }
        String[] parts = cacheKey.split(":");
        if (parts.length < 3) {
            return cacheKey;
        }
        return String.join(":", java.util.Arrays.copyOfRange(parts, 0, 3));
    }

    private static String normalizeKey(String value) {
        if (value == null) {
            return "null";
        }
        return value.trim().replaceAll("[^a-zA-Z0-9-_.]", "-").toLowerCase();
    }
}
