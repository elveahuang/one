package cc.wdev.platform.commons.autoconfigure.ai.properties;

import cc.wdev.platform.commons.ai.config.SplittingConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author elvea
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = AiVectorStoreElasticsearchProperties.PREFIX)
public class AiTokenTextSplitterProperties extends SplittingConfig {

    public static final String PREFIX = "platform.ai.rag.redis";

    private boolean enabled = false;

}
