package com.vietanh.webmanh.services.impl;

import com.vietanh.webmanh.dbs.postgres.repositories.DailyViewCountStatisticRepo;
import com.vietanh.webmanh.dbs.postgres.specs.ComicStatisticProjection;
import com.vietanh.webmanh.services.StatisticService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticServiceImpl implements StatisticService {
    DailyViewCountStatisticRepo dailyViewCountStatisticRepo;

    @Override
    public List<ComicStatisticProjection> topComicByDay(int limit) {
        LocalDateTime to = LocalDate.now().atStartOfDay().plusDays(1);
        LocalDateTime from = to.minusDays(1);
        return dailyViewCountStatisticRepo.findTopComicByTimeRange(from, to, limit);
    }

    @Override
    public List<ComicStatisticProjection> topComicByWeek(int limit) {
        LocalDateTime to = LocalDate.now().atStartOfDay().plusDays(1);
        LocalDateTime from = to.minusWeeks(1);
        return dailyViewCountStatisticRepo.findTopComicByTimeRange(from, to, limit);
    }

    @Override
    public List<ComicStatisticProjection> topComicByMonth(int limit) {
        LocalDateTime to = LocalDate.now().atStartOfDay().plusDays(1);
        LocalDateTime from = to.minusMonths(1);
        return dailyViewCountStatisticRepo.findTopComicByTimeRange(from, to, limit);
    }
}
