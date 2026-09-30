package cc.wdev.platform.commons.utils.time;

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * @author elvea
 */
public interface TimeZoneResolver {

    /**
     * 获取用户时区
     */
    ZoneId resolveUserZoneId();

    /**
     * 获取用户时区
     */
    TimeZone resolveUserTimeZone();

    /**
     * 获取系统时区
     */
    ZoneId resolveSystemZoneId();

    /**
     * 获取系统时区
     */
    TimeZone resolveSystemTimeZone();

}
