package ar.com.greenbundle.haylugar.util.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public class TimeZoneUtils {
    private final static ZoneId UTC_ZONE = ZoneId.of("UTC");
    public static String representationDateTimeUTCToZone(LocalDateTime dateTime, ZoneId representationZone) {
        if(dateTime == null)
            return null;

        return dateTime.atZone(UTC_ZONE)
                .withZoneSameInstant(representationZone)
                .toLocalDateTime().toString();
    }

    public static String representationTimeUTCToZone(LocalTime time, ZoneId representationZone) {
        if(time == null)
            return null;

        return time.atDate(LocalDate.now())
                .atZone(UTC_ZONE)
                .withZoneSameInstant(representationZone)
                .toLocalDateTime().toString();
    }
}
