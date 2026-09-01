package cc.wdev.webapp.configuration;

import cc.wdev.webapp.ai.tools.CoreTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author elvea
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class AiConfiguration {

    @Bean
    public ToolCallbackProvider commonToolsProvider(CoreTools coreTools) {
        return MethodToolCallbackProvider.builder().toolObjects(coreTools).build();
    }

}
