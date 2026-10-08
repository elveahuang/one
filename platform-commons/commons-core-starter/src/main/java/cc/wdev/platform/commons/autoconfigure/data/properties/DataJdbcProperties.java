package cc.wdev.platform.commons.autoconfigure.data.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(DataJdbcProperties.PREFIX)
public class DataJdbcProperties {

    public static final String PREFIX = "platform.data.jdbc";

    private boolean enabled = false;

}
