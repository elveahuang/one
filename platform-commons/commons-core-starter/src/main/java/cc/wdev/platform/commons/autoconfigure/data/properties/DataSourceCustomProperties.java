package cc.wdev.platform.commons.autoconfigure.data.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author elvea
 */
@Data
@ConfigurationProperties(DataSourceCustomProperties.PREFIX)
public class DataSourceCustomProperties {

    public static final String PREFIX = "platform.data.datasource";

    public static final String MASTER_DATASOURCE_PREFIX = "platform.data.datasource.master";

    public static final String SLAVE_DATASOURCE_PREFIX = "platform.data.datasource.slave";

    public static final String JOB_DATASOURCE_PREFIX = "platform.data.datasource.job";

    private boolean enabled = false;

    private boolean masterSlaveEnabled = false;

    @NestedConfigurationProperty
    private DataSourceCustomProperties.Config master = new Config();

    @NestedConfigurationProperty
    private DataSourceCustomProperties.Config slave = new Config();

    @NestedConfigurationProperty
    private DataSourceCustomProperties.Config job = new Config();

    @Data
    public static class Config {

        private boolean enabled = false;

    }

}
