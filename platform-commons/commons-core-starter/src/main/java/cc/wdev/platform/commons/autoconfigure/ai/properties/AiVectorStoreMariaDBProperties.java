package cc.wdev.platform.commons.autoconfigure.ai.properties;

import cc.wdev.platform.commons.ai.factory.vectorstore.MariaDBVectorStoreConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author elvea
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = AiVectorStoreMariaDBProperties.PREFIX)
public class AiVectorStoreMariaDBProperties extends MariaDBVectorStoreConfig {

    public static final String PREFIX = "platform.ai.vectorstore.mariadb";

    private boolean enabled = false;

}
