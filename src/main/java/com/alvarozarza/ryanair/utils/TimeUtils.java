package com.alvarozarza.ryanair.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeUtils {

    //Check if is the next day to add another day to the date
    public static Duration durationBetweenTimes(LocalTime start, LocalTime end) {
        Duration duration = Duration.between(start, end);
        return duration.isNegative() == true ? Duration.ofDays(1).plusMinutes(duration.toMinutes()) : duration;
    }

    public static Duration durationBetweenDates(LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);
        if (duration.isNegative() && start.getDayOfYear() < end.getDayOfYear()) {
            return Duration.ofDays(1).plusMinutes(duration.toMinutes());
        } else return duration;

    }

}
