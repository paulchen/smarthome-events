package at.rueckgr.smarthome.events.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public final class TimeHelper {
    private TimeHelper() {
        // no instances
    }

    public static LocalDateTime toLocalDateTime(final Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), getTimezone());
    }

    public static Date toDate(final LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(getTimezone()).toInstant());
    }

    public static long getAgeInMinutes(final LocalDateTime localDateTime) {
        Duration duration = Duration.between(localDateTime, LocalDateTime.now());
        return duration.toMinutes();
    }

    private static ZoneId getTimezone() {
        return ZoneId.systemDefault();
    }
}
