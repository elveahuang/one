package cc.wdev.platform.commons.utils.time;

import cc.wdev.platform.commons.constants.DateTimeConstants;

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * 默认时区解析器
 * 直接返回当前系统的默认时区。
 *
 * @author elvea
 */
public class DefaultTimeZoneResolver implements TimeZoneResolver {

    /**
     * 用户时区
     */
    private final ZoneId userZoneId;

    /**
     * 系统时区
     */
    private final ZoneId systemZoneId;

    public DefaultTimeZoneResolver() {
        this(DateTimeConstants.ZONE_ID_DEFAULT, DateTimeConstants.ZONE_ID_DEFAULT);
    }

    public DefaultTimeZoneResolver(ZoneId userZoneId, ZoneId systemZoneId) {
        this.userZoneId = userZoneId;
        this.systemZoneId = systemZoneId;
    }

    /**
     * @see TimeZoneResolver#resolveUserZoneId()
     */
    @Override
    public ZoneId resolveUserZoneId() {
        return this.userZoneId;
    }

    /**
     * @see TimeZoneResolver#resolveUserTimeZone()
     */
    @Override
    public TimeZone resolveUserTimeZone() {
        return TimeZone.getTimeZone(resolveUserZoneId());
    }

    /**
     * @see TimeZoneResolver#resolveSystemZoneId()
     */
    @Override
    public ZoneId resolveSystemZoneId() {
        return this.systemZoneId;
    }

    /**
     * @see TimeZoneResolver#resolveSystemTimeZone()
     */
    @Override
    public TimeZone resolveSystemTimeZone() {
        return TimeZone.getTimeZone(resolveSystemZoneId());
    }

}
