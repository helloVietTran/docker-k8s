package com.vietanh.webmanh.services;

import java.util.List;

public interface StatisticService {
    List<?> topComicByDay(int limit);
    List<?> topComicByWeek(int limit);
    List<?> topComicByMonth(int limit);
}
