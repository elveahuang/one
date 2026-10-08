package cc.wdev.platform.commons.autoconfigure.extensions.properties;

import cc.wdev.platform.commons.enums.SeleniumDriverTypeEnum;
import cc.wdev.platform.commons.extensions.http.HttpProxy;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(SeleniumProperties.PREFIX)
public class SeleniumProperties {

    public static final String PREFIX = "platform.selenium";

    private boolean enabled = false;

    private SeleniumDriverTypeEnum driverType = SeleniumDriverTypeEnum.EDGE;

    private String driverVersion;

    @NestedConfigurationProperty
    private HttpProxy proxy = HttpProxy.builder().build();

}
