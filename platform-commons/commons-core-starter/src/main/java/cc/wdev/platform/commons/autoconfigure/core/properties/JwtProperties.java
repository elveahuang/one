package cc.wdev.platform.commons.autoconfigure.core.properties;

import cc.wdev.platform.commons.utils.jwt.JwtStrategy;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;

import java.time.Duration;

/**
 * @author elvea
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(JwtProperties.PREFIX)
public class JwtProperties {

    public static final String PREFIX = "platform.jwt";

    private boolean enabled = true;

    private JwtStrategy strategy = JwtStrategy.AUTO;

    private String algorithm = JwsAlgorithms.RS256;

    private String publicKeyValue;

    private String privateKeyValue;

    private Duration authorizationCodeTimeToLive = Duration.ofMinutes(5);

    private Duration deviceCodeTimeToLive = Duration.ofMinutes(5);

    private Duration accessTokenTimeToLive = Duration.ofMinutes(60);

    private Duration refreshTokenTimeToLive = Duration.ofDays(14);

}
