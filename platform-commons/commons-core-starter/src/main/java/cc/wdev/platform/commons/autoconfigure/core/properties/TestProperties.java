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
@ConfigurationProperties(TestProperties.PREFIX)
public class TestProperties implements Serializable {

    public static final String PREFIX = "platform.test";

    private boolean enabled = false;

    private String email;

    private String mobileCountryCode = "0086";

    private String mobileNumber;

}
