package cc.wdev.platform.commons.core.cache;

import cc.wdev.platform.commons.constants.GlobalConstants;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import java.time.Duration;

import static cc.wdev.platform.commons.utils.NumberUtils.randomInteger;

/**
 * @author elvea
 */
@Getter
@Setter
public class CacheKey {

    @NonNull
    private String key;

    private Duration expire = GlobalConstants.DEFAULT_CACHE_DURATION;

    public CacheKey(final @NonNull String key) {
        this.key = key;
    }

    public CacheKey(final @NonNull String key, final @NonNull Duration expire) {
        this.key = key;
        this.expire = expire;
    }

    /**
     * 过期时间在默认设定的数值基础上增加一个随机数
     */
    public Duration getExpire() {
        return expire.plusSeconds(randomInteger(10));
    }

}
