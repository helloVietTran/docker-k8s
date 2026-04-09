package com.viettran.reading_story_web.scheduler;

import java.util.List;
import java.util.Set;

import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.viettran.reading_story_web.entity.mysql.Story;
import com.viettran.reading_story_web.repository.jpa.StoryRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StoryJobScheduler {
    StoryRepository storyRepository;
    StringRedisTemplate stringRedisTemplate;

    @Scheduled(cron = "0 */5 * * * ?")
    public void syncAndResetStoryViews() {
        Set<String> keys = stringRedisTemplate.keys("story::*");

        if (keys != null) {
            for (String key : keys) {
                String viewCountStr = stringRedisTemplate.opsForValue().get(key);
                if (viewCountStr != null) {
                    String[] keyParts = key.split("::");
                    if (keyParts.length == 2) {
                        Integer storyId = Integer.parseInt(keyParts[1]);
                        updateStoryViewCountInMySQL(storyId, viewCountStr);
                    }
                }
            }

            // Sau khi đồng bộ xong: reset toàn bộ key về 0 trong Redis
            stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                if (connection instanceof StringRedisConnection conn) {
                    for (String key : keys) {
                        conn.set(key, "0");
                    }
                }
                return null;
            });
        }
    }

    // chạy vào 1h sáng 0 phút 30 giây
    @Scheduled(cron = "30 0 1 * * ?")
    public void updateHotStory() {
        List<Story> stories = storyRepository.findByViewCountGreaterThanAndHotFalse(20);

        if (!stories.isEmpty()) {
            for (Story story : stories) {
                story.setHot(true); // Cập nhật trường hot thành true
            }
            storyRepository.saveAll(stories);
        }
    }

    private void updateStoryViewCountInMySQL(Integer storyId, String viewCountStr) {
        int viewCount = Integer.parseInt(viewCountStr);
        Story story = storyRepository.findById(storyId).orElse(null);

        if (story != null) {
            story.setViewCount(story.getViewCount() + viewCount);
            storyRepository.save(story);
        }
    }
}
