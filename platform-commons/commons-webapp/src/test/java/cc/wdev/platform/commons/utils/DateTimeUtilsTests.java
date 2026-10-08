package cc.wdev.platform.commons.utils;

import cc.wdev.platform.commons.constants.DateTimeConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

/**
 * @author elvea
 */
@Slf4j
public class DateTimeUtilsTests {

    @Test
    public void base() {
        Instant issuedAt = Instant.now();
        Date now = new Date();
        String sourceText, targetText;
        LocalDateTime sourceLdt, targetLdt;
        LocalDate targetLocalDate;
        LocalTime targetLocalTime;

        sourceText = "2023-01-01 12:00:00.000";
        sourceLdt = DateTimeUtils.parse(sourceText, DateTimeConstants.Pattern.FULL_DATE_TIME, LocalDateTime.class);
        targetLdt = DateTimeUtils.transfer(sourceLdt, DateTimeConstants.ZONE_ID_UTC);
        targetText = DateTimeUtils.format(targetLdt, DateTimeConstants.Pattern.FULL_DATE_TIME);
        Assertions.assertEquals(targetText, "2023-01-01 04:00:00.000");

        targetLdt = DateTimeUtils.transfer(sourceLdt, DateTimeConstants.ZONE_ID_UTC, DateTimeConstants.ZONE_ID_CHINA);
        targetText = DateTimeUtils.format(targetLdt, DateTimeConstants.Pattern.FULL_DATE_TIME);
        Assertions.assertEquals(targetText, "2023-01-01 20:00:00.000");

        targetLdt = DateTimeUtils.toLocalDateTime(now);
        targetLocalTime = DateTimeUtils.toLocalTime(now);
        targetLocalDate = DateTimeUtils.toLocalDate(now);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        Assertions.assertEquals(calendar.get(Calendar.YEAR), targetLdt.getYear());
        Assertions.assertEquals(calendar.get(Calendar.MONTH) + 1, targetLdt.getMonthValue());
        Assertions.assertEquals(calendar.get(Calendar.DATE), targetLdt.getDayOfMonth());
        Assertions.assertEquals(calendar.get(Calendar.HOUR_OF_DAY), targetLdt.getHour());
        Assertions.assertEquals(calendar.get(Calendar.MINUTE), targetLdt.getMinute());
        Assertions.assertEquals(calendar.get(Calendar.SECOND), targetLdt.getSecond());

        Assertions.assertEquals(calendar.get(Calendar.YEAR), targetLocalDate.getYear());
        Assertions.assertEquals(calendar.get(Calendar.MONTH) + 1, targetLocalDate.getMonthValue());
        Assertions.assertEquals(calendar.get(Calendar.DATE), targetLocalDate.getDayOfMonth());

        Assertions.assertEquals(calendar.get(Calendar.HOUR_OF_DAY), targetLocalTime.getHour());
        Assertions.assertEquals(calendar.get(Calendar.MINUTE), targetLocalTime.getMinute());
        Assertions.assertEquals(calendar.get(Calendar.SECOND), targetLocalTime.getSecond());
    }

}
