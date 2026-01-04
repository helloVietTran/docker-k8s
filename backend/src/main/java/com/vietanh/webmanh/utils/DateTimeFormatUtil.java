package com.vietanh.webmanh.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Component;

@Component
public class DateTimeFormatUtil {
    Map<Long, Function<Instant, String>> strategyMap = new LinkedHashMap<>();

    public DateTimeFormatUtil() {
        strategyMap.put(60L, this::formatBySeconds);
        strategyMap.put(3600L, this::formatByMinutes);
        strategyMap.put(86400L, this::formatByHours);
        strategyMap.put(2592000L, this::formatByDays);
        strategyMap.put(31536000L, this::formatByMonths);
        strategyMap.put(Long.MAX_VALUE, this::formatByYears);
    }


    public String format(Instant instant) {
        long duration = ChronoUnit.SECONDS.between(instant, Instant.now());

        var stragegy = strategyMap.entrySet().stream()
                .filter(longFunctionEntry -> duration < longFunctionEntry.getKey())
                .findFirst()
                .get();

        return stragegy.getValue().apply(instant);
    }

    private String formatBySeconds(Instant instant) {
        long eclapseSeconds = ChronoUnit.SECONDS.between(instant, Instant.now());
        return eclapseSeconds + " giây trước";
    }

    private String formatByMinutes(Instant instant) {
        long eclapseMinutes = ChronoUnit.MINUTES.between(instant, Instant.now());
        return eclapseMinutes + " phút trước";
    }

    private String formatByHours(Instant instant) {
        long eclapseHours = ChronoUnit.HOURS.between(instant, Instant.now());
        return eclapseHours + " giờ trước";
    }

    private String formatByDays(Instant instant) {
        long eclapseDays = ChronoUnit.DAYS.between(instant, Instant.now());
        return eclapseDays + " ngày trước";
    }

    private String formatByMonths(Instant instant) {
        long eclapseMonths = ChronoUnit.MONTHS.between(instant, Instant.now());
        return eclapseMonths + " tháng trước";
    }

    private String formatByYears(Instant instant) {
        long eclapseDays = ChronoUnit.YEARS.between(instant, Instant.now());
        return eclapseDays + " năm trước";
    }
}