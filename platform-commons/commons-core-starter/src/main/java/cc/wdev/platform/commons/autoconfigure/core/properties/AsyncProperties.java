package cc.wdev.platform.commons.autoconfigure.core.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = AsyncProperties.PREFIX)
public class AsyncProperties {

    public static final String PREFIX = "platform.async";

    private int corePoolSize = 10;

    private int maxPoolSize = 50;

    private int keepAliveSeconds = 60;

    private int queueCapacity = 10000;

    private String threadNamePrefix = "async-thread-";

}
