package cc.wdev.platform.commons.autoconfigure.ai.properties;

import cc.wdev.platform.commons.ai.factory.vectorstore.PgVectorStoreConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author elvea
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = AiVectorStorePgVectorProperties.PREFIX)
public class AiVectorStorePgVectorProperties extends PgVectorStoreConfig {

    public static final String PREFIX = "platform.ai.vectorstore.pgvector";

    private boolean enabled = false;

}
