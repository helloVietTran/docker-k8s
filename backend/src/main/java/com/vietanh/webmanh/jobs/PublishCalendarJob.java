package com.vietanh.webmanh.jobs;

import com.vietanh.webmanh.constants.ComicStatus;
import com.vietanh.webmanh.constants.ReleaseStatus;
import com.vietanh.webmanh.dbs.postgres.models.Comic;
import com.vietanh.webmanh.dbs.postgres.models.ReleaseCalendar;
import com.vietanh.webmanh.dbs.postgres.repositories.ComicRepository;
import com.vietanh.webmanh.dbs.postgres.repositories.ReleaseCalendarRepository;
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

//    private static final String LOCK_KEY = "publish_calendar:lock";
//    private static final int MAX_RETRY = 2;
//
//    private final StringRedisTemplate redisTemplate;
//    private final ReleaseCalendarRepository releaseCalendarRepository;
//    private final ComicRepository comicRepository;
//
//    @Transactional
//    @Scheduled(fixedDelay = 60_000)
//    public void processPublishCalendar() {
//
//        Boolean acquired = redisTemplate.opsForValue()
//                .setIfAbsent(LOCK_KEY, "1", Duration.ofSeconds(30));
//
//        if (Boolean.FALSE.equals(acquired)) {
//            return;
//        }
//
//        try {
//            Instant now = Instant.now();
//
//            List<ReleaseCalendar> calendars = releaseCalendarRepository.findDuePublish(
//                            ReleaseStatus.SCHEDULED, now
//                    );
//
//            for (ReleaseCalendar calendar : calendars) {
//
//                // Skip if retry enough
//                if (calendar.getRetryCount() >= MAX_RETRY) {
//                    calendar.setReleaseStatus(ReleaseStatus.FAILED);
//                    continue;
//                }
//
//                try {
//                    if (calendar.getTargetType() == TargetType.COMIC) {
//                        publishComic(calendar);
//                    }
//                    // else if (CHAPTER) { TODO }
//
//                    calendar.setReleaseStatus(ReleaseStatus.PUBLISHED);
//                    calendar.setPublishedAt(now);
//
//                } catch (Exception e) {
//                    int retry = calendar.getRetryCount() + 1;
//                    calendar.setRetryCount(retry);
//
//                    log.error(
//                            "Publish failed, calendarId={}, retry={}",
//                            calendar.getPublishCalendarId(),
//                            retry,
//                            e
//                    );
//
//                    if (retry >= MAX_RETRY) {
//                        calendar.setReleaseStatus(ReleaseStatus.FAILED);
//                    }
//                }
//            }
//
//        } finally {
//            redisTemplate.delete(LOCK_KEY);
//        }
//    }
//
//    private void publishComic(ReleaseCalendar calendar) {
//        Comic comic = comicRepository.findById(calendar.getTargetId())
//                .orElseThrow(() ->
//                        new IllegalStateException("Comic not found: " + calendar.getTargetId())
//                );
//
//        comic.setStatus(ComicStatus.ON_GOING);
//        comicRepository.save(comic);
//    }
}
