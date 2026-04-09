package com.viettran.reading_story_web.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
        strategyMap.put(Long.MAX_VALUE, this::formatByDays);
    }

    public String dateTimePatternFormat(Instant instant, String pattern) {
        LocalDate localDate = instant.atZone(ZoneId.of("UTC")).toLocalDate();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        String formattedDate = localDate.format(formatter);

        return formattedDate;
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
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE;

        return localDateTime.format(dateTimeFormatter);
    }
}
// Strategy Pattern
