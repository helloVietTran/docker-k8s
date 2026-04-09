package com.viettran.reading_story_web.scheduler;

import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.viettran.reading_story_web.entity.mysql.Chapter;
import com.viettran.reading_story_web.repository.jpa.ChapterRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChapterJobScheduler {
    ChapterRepository chapterRepository;
    StringRedisTemplate stringRedisTemplate;

    @Scheduled(cron = "0 */5 * * * ?")
    public void syncAndResetChapterViews() {
        Set<String> keys = stringRedisTemplate.keys("chapter::*");

        if (keys != null) {
            for (String key : keys) {
                String viewCountStr = stringRedisTemplate.opsForValue().get(key);

                if (viewCountStr != null) {
                    String[] keyParts = key.split("::");
                    if (keyParts.length == 2) {
                        String chapterId = keyParts[1];

                        updateChapterViewsCount(chapterId, viewCountStr);

                        // Reset lại sau khi cập nhật
                        stringRedisTemplate.opsForValue().set(key, "0");
                    }
                }
            }
        }
    }

    private void updateChapterViewsCount(String chapterId, String viewCountStr) {
        int viewCount = Integer.parseInt(viewCountStr);
        Chapter chapter = chapterRepository.findById(chapterId).orElse(null);

        if (chapter != null) {
            chapter.setViewCount(viewCount);
            chapterRepository.save(chapter);
        }
    }
}
