package cc.wdev.platform.commons.autoconfigure.extensions.properties;

import cc.wdev.platform.commons.extensions.ip.geoip2.GeoLiteConfig;
import cc.wdev.platform.commons.extensions.ip.ip2region.Ip2RegionConfig;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(IpProperties.PREFIX)
public class IpProperties {

    public static final String PREFIX = "platform.ip";

    private boolean enabled = false;

    @NestedConfigurationProperty
    private GeoLiteConfig geoLite = new GeoLiteConfig();

    @NestedConfigurationProperty
    private Ip2RegionConfig ip2region = new Ip2RegionConfig();

}
