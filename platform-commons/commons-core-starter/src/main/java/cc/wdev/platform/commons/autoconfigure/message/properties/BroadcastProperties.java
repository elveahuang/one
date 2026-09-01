package cc.wdev.platform.commons.autoconfigure.message.properties;

import cc.wdev.platform.commons.message.topic.BroadcastType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = BroadcastProperties.PREFIX)
public class BroadcastProperties implements Serializable {

    public static final String PREFIX = "platform.message.broadcast";

    private boolean enabled = false;

    private Long nodeId = 1L;

    private BroadcastType type = BroadcastType.Rabbit;

}
