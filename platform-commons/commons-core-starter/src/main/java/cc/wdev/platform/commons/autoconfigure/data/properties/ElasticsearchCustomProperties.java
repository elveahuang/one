package cc.wdev.platform.commons.autoconfigure.data.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(ElasticsearchCustomProperties.PREFIX)
public class ElasticsearchCustomProperties {

    public static final String PREFIX = "platform.data.elasticsearch";

    private boolean enabled = false;

}
