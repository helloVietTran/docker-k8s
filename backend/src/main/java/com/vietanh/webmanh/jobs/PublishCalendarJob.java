package com.vietanh.webmanh.jobs;

import com.vietanh.webmanh.constants.ComicStatus;
import com.vietanh.webmanh.constants.PublishStatus;
import com.vietanh.webmanh.constants.PublishTargetType;
import com.vietanh.webmanh.dbs.postgres.models.Comic;
import com.vietanh.webmanh.dbs.postgres.models.PublishCalendar;
import com.vietanh.webmanh.dbs.postgres.repositories.ComicRepository;
import com.vietanh.webmanh.dbs.postgres.repositories.PublishCalendarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublishCalendarJob {

    private static final String LOCK_KEY = "publish_calendar:lock";
    private static final int MAX_RETRY = 2;

    private final StringRedisTemplate redisTemplate;
    private final PublishCalendarRepository publishCalendarRepository;
    private final ComicRepository comicRepository;

    @Transactional
    @Scheduled(fixedDelay = 60_000)
    public void processPublishCalendar() {

        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(LOCK_KEY, "1", Duration.ofSeconds(30));

        if (Boolean.FALSE.equals(acquired)) {
            return;
        }

        try {
            Instant now = Instant.now();

            List<PublishCalendar> calendars = publishCalendarRepository.findDuePublish(
                            PublishStatus.SCHEDULED, now
                    );

            for (PublishCalendar calendar : calendars) {

                // Skip if retry enough
                if (calendar.getRetryCount() >= MAX_RETRY) {
                    calendar.setPublishStatus(PublishStatus.FAILED);
                    continue;
                }

                try {
                    if (calendar.getPublishTargetType() == PublishTargetType.COMIC) {
                        publishComic(calendar);
                    }
                    // else if (CHAPTER) { TODO }

                    calendar.setPublishStatus(PublishStatus.PUBLISHED);
                    calendar.setPublishedAt(now);

                } catch (Exception e) {
                    int retry = calendar.getRetryCount() + 1;
                    calendar.setRetryCount(retry);

                    log.error(
                            "Publish failed, calendarId={}, retry={}",
                            calendar.getPublishCalendarId(),
                            retry,
                            e
                    );

                    if (retry >= MAX_RETRY) {
                        calendar.setPublishStatus(PublishStatus.FAILED);
                    }
                }
            }

        } finally {
            redisTemplate.delete(LOCK_KEY);
        }
    }

    private void publishComic(PublishCalendar calendar) {
        Comic comic = comicRepository.findById(calendar.getTargetId())
                .orElseThrow(() ->
                        new IllegalStateException("Comic not found: " + calendar.getTargetId())
                );

        comic.setStatus(ComicStatus.ON_GOING);
        comicRepository.save(comic);
    }
}
