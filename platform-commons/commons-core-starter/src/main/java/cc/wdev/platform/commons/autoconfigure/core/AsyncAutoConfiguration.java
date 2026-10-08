package cc.wdev.platform.commons.autoconfigure.core;

import cc.wdev.platform.commons.autoconfigure.core.properties.AsyncProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author elvea
 */
@Slf4j
@EnableAsync
@AutoConfiguration
@ConditionalOnProperty(prefix = AsyncProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({AsyncProperties.class})
public class AsyncAutoConfiguration {

    public AsyncAutoConfiguration() {
        log.info("AsyncAutoConfiguration is enabled");
    }

    @Bean(name = "virtualThreadTaskExecutor")
    public TaskExecutor virtualThreadTaskExecutor() {
        return new VirtualThreadTaskExecutor("vt-async-");
    }

}
