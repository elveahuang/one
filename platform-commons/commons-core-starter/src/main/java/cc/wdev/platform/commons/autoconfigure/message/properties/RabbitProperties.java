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
@ConfigurationProperties(prefix = RabbitProperties.PREFIX)
public class RabbitProperties implements Serializable {

    public static final String PREFIX = "platform.message.rabbit";

}
