package cc.wdev.platform.commons.utils.time;

import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author elvea
 */
@Slf4j
public class TimeZoneManager {

    private static final TimeZoneResolver resolver = new DefaultTimeZoneResolver();

    public static TimeZoneResolver getResolver() {
        return ObjectUtils.nvl(SpringUtils.getBean(TimeZoneResolver.class), resolver);
    }

}
