package cc.wdev.platform.commons.autoconfigure.message.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = WebSocketProperties.PREFIX)
public class WebSocketProperties implements Serializable {

    public static final String PREFIX = "platform.websocket";

    private boolean enabled = true;

}
