package cc.wdev.platform.commons.autoconfigure.core.properties;

import cc.wdev.platform.commons.core.cache.enums.CacheCodecTypeEnum;
import cc.wdev.platform.commons.core.cache.enums.CacheManagerProviderEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = CacheCustomProperties.PREFIX)
public class CacheCustomProperties {

    public static final String PREFIX = "platform.cache";

    public static final String PREFIX_PROVIDER = "platform.cache.provider";

    private boolean enabled = false;

    private CacheCodecTypeEnum codec = CacheCodecTypeEnum.JACKSON;

    private CacheManagerProviderEnum provider = CacheManagerProviderEnum.REDISSON;

    private boolean cacheNullValue = true;

    private int batchSize = 1000;

    private Duration timeToLive = Duration.ofHours(1);

}
