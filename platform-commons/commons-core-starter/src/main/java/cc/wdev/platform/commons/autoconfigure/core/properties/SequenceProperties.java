package cc.wdev.platform.commons.autoconfigure.core.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = SequenceProperties.PREFIX)
public class SequenceProperties implements Serializable {

    public static final String PREFIX = "platform.sequence";

    private boolean enabled = false;

}
