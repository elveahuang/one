package cc.wdev.platform.commons.autoconfigure.ai.properties;

import cc.wdev.platform.commons.ai.factory.vectorstore.ElasticsearchVectorStoreConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author elvea
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = AiVectorStoreElasticsearchProperties.PREFIX)
public class AiVectorStoreElasticsearchProperties extends ElasticsearchVectorStoreConfig {

    public static final String PREFIX = "platform.ai.vectorstore.elasticsearch";

    private boolean enabled = false;

}
