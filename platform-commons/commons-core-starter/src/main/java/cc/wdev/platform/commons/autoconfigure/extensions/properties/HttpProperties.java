package cc.wdev.platform.commons.autoconfigure.extensions.properties;

import cc.wdev.platform.commons.extensions.http.HttpDebug;
import cc.wdev.platform.commons.extensions.http.HttpProxy;
import cc.wdev.platform.commons.extensions.http.enums.HttpClientTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(HttpProperties.PREFIX)
public class HttpProperties {

    public static final String PREFIX = "platform.http";

    private boolean enabled = true;

    private HttpClientTypeEnum type = HttpClientTypeEnum.OKHTTP;

    @NestedConfigurationProperty
    private HttpDebug debug = HttpDebug.builder().build();

    @NestedConfigurationProperty
    private HttpProxy proxy = HttpProxy.builder().build();

}
