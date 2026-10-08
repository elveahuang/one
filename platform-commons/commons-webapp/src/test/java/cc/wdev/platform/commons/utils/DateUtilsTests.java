package cc.wdev.platform.commons.utils;

import cc.wdev.platform.commons.constants.DateTimeConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static cc.wdev.platform.commons.constants.DateTimeConstants.EPOCH;

/**
 * @author elvea
 */
@Slf4j
public class DateUtilsTests {

    @Test
    public void epochTest() {
        Date date = new Date(EPOCH);
        System.out.println(DateUtils.format(date, DateTimeConstants.Pattern.FULL_DATE_TIME));
        Assertions.assertNotNull(date);

        date = DateUtils.parse("2000-01-01", DateTimeConstants.Pattern.DATE);
        System.out.println(date.getTime());
    }

    @Test
    public void base() {
        String sourceText, targetText;
        Date sourceDate, targetDate;

        sourceText = "2023-01-01 12:00:00.000";
        sourceDate = DateUtils.parse(sourceText, DateTimeConstants.Pattern.FULL_DATE_TIME, DateTimeConstants.TIME_ZONE_CHINA);
        targetText = DateUtils.format(sourceDate, DateTimeConstants.Pattern.FULL_DATE_TIME, DateTimeConstants.TIME_ZONE_UTC);
        Assertions.assertEquals(targetText, "2023-01-01 04:00:00.000");

        sourceText = "2023-01-01";
        sourceDate = DateUtils.parseDate(sourceText);
        targetText = DateUtils.format(sourceDate, DateTimeConstants.Pattern.DATE);
        Assertions.assertEquals(sourceText, targetText);
    }

}
