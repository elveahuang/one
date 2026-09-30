package cc.wdev.platform.commons.utils.jwt;

import org.springframework.security.oauth2.jwt.*;

/**
 * @author elvea
 */
public record JwtService(JwtConfig config, JwtEncoder encoder, JwtDecoder decoder) {

    /**
     * @param claimsSet 1 {@link JwtClaimsSet}
     * @return {@link Jwt}
     */
    public Jwt generateJwtToken(JwtClaimsSet claimsSet) {
        return this.encoder.encode(JwtEncoderParameters.from(claimsSet));
    }

    /**
     * @param token String
     * @return {@link Jwt}
     */
    public Jwt parseJwtToken(String token) {
        return this.decoder.decode(token);
    }

}
