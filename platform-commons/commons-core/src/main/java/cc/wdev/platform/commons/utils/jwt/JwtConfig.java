package cc.wdev.platform.commons.utils.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtConfig {

    @Builder.Default
    private Boolean enabled = Boolean.FALSE;

    @Builder.Default
    private Duration authorizationCodeTimeToLive = Duration.ofMinutes(5);

    @Builder.Default
    private Duration deviceCodeTimeToLive = Duration.ofMinutes(5);

    @Builder.Default
    private Duration accessTokenTimeToLive = Duration.ofMinutes(15);

    @Builder.Default
    private Duration refreshTokenTimeToLive = Duration.ofDays(3);

}
