package cc.wdev.platform.commons.oapis.dingtalk.cache;

import java.util.concurrent.TimeUnit;

/**
 * @author elvea
 */
public interface Cache {

    String get(String key);

    void set(String key, String value, long expire, TimeUnit timeUnit);

}
