package cc.wdev.platform.commons.autoconfigure.oapis.properties;

import cc.wdev.platform.commons.extensions.http.HttpProxy;
import cc.wdev.platform.commons.oapis.telegram.TelegramConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = TelegramProperties.PREFIX)
public class TelegramProperties implements Serializable {

    public static final String PREFIX = "platform.oapis.telegram";

    private boolean enabled = false;

    @NestedConfigurationProperty
    private HttpProxy proxy = HttpProxy.builder().build();

    @NestedConfigurationProperty
    private TelegramConfig bot = TelegramConfig.builder().build();

}
