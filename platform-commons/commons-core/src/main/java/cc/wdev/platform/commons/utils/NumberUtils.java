package cc.wdev.platform.commons.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadLocalRandom;

import static java.util.Objects.isNull;

/**
 * @author elvea
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class NumberUtils {

    public static Long nvl(Long value) {
        return nvl(value, 0L);
    }

    public static Long nvl(Long value, Long defaultValue) {
        return isNull(value) ? defaultValue : value;
    }

    public static String toString(Number num) {
        if (num == null) {
            return null;
        }
        return String.valueOf(num);
    }

    public static Long toLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            log.info("Invalid Long format: {}", str);
            return null;
        }
    }

    public static int toInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static Integer toInteger(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            log.info("Invalid Integer format: {}", str);
            return null;
        }
    }

    public static int randomInteger(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

}
